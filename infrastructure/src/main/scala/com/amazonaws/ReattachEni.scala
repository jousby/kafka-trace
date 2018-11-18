package com.amazonaws


import com.amazonaws.services.autoscaling.model.CompleteLifecycleActionRequest
import com.amazonaws.services.autoscaling.{AmazonAutoScalingClient, AmazonAutoScalingClientBuilder}
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}


/**
  * The object passed to the lambda function is a json struct converted to a java.util.HashMap[String, Object]. In this
  * case we are processing an Autoscaling Lifecycle event from Cloudwatch. The following case classes models the
  * attributes we need for this particular function.
  *
  * {
  *   "version": "0",
  *   "id": "12345678-1234-1234-1234-123456789012",
  *   "detail-type": "EC2 Instance-launch Lifecycle Action",
  *   "source": "aws.autoscaling",
  *   "account": "123456789012",
  *   "time": "yyyy-mm-ddThh:mm:ssZ",
  *   "region": "us-west-2",
  *   "resources": [
  *     "auto-scaling-group-arn"
  *   ],
  *   "detail": {
  *     "LifecycleActionToken": "87654321-4321-4321-4321-210987654321",
  *     "AutoScalingGroupName": "my-asg",
  *     "LifecycleHookName": "my-lifecycle-hook",
  *     "EC2InstanceId": "i-1234567890abcdef0",
  *     "LifecycleTransition": "autoscaling:EC2_INSTANCE_LAUNCHING",
  *     "NotificationMetadata": "additional-info"
  *   }
  * }
  */
case class Detail(
  autoScalingGroupName: String,
  lifecycleHookName: String,
  ec2InstanceId: String
)

case class ASGEvent(
  id: String,
  detail: Detail
)

/**
  * Implements the AWS Lambda function RequestHandler interface to provide functionality for reattaching an existing
  * network interface to an ec2 instance that has been restarted (auto healed).
  *
  * This is useful for applications that have the ip address of ec2 instances harded coded in some way. i.e Zookeeper
  * has a config file that has the ip address of all nodes in the cluster pre defined. If a load balancer recycles an
  * ec2 instance in a particular AZ for some reason then this lambda function will look to ensure that it comes back
  * on line with the same IP address.
  */
class ReattachEni extends RequestHandler[java.util.HashMap[String, Object], Unit] {

  type FailureReason = String

  val ec2Client = AmazonEC2ClientBuilder.defaultClient()
  val asgClient = AmazonAutoScalingClientBuilder.defaultClient()

  /**
    * The AWS Lambda function contract. In this function we are expecting to be passed an Autoscaling Lifecycle Event
    * (see definition above) as a java.util.HashMap. We look to find the network interface that was attached the old
    * instance that this new instance is replacing an re attach it to this new instance.
    *
    * @param eventMap A json autoscaling lifecycle event struct converted to a hashmap
    * @param context The lambda execution environment
    */
  override def handleRequest(eventMap: java.util.HashMap[String, Object], context: Context): Unit = {
    val logger = context.getLogger

    if (isAutoscalingGroupEvent(eventMap)) {
      toASGEvent(eventMap) match {
        case Right(asgEvent) => {
          logger.log(s"About to try reattaching eni to new instance: ${asgEvent.detail.ec2InstanceId}")
          handleRequest(asgEvent, context)
            match {
              case Success(attachmentId) => {
                logger.log(s"Successfully attached eni to instance: ${asgEvent.detail.ec2InstanceId}")
                completeLifecycleAction(asgEvent, true)
              }
              case Failure(exception) => {
                logger.log(s"Exception when trying to reattach eni: ${exception.getMessage}")
                completeLifecycleAction(asgEvent, false)
              }
          }
        }
        case Left(error) => {
          logger.log(s"Error mapping eventMap to asgEvent object - error = $error")
        }
      }
    }
  }

  // Check that our hash map contains the expected autoscaling event
  def isAutoscalingGroupEvent(event: java.util.HashMap[String, Object]): Boolean =
    getValue(event, "detail-type") match {
      case Right(eventType) => eventType == "EC2 Instance-launch Lifecycle Action"
      case Left(_) => false
    }

  // Convert the event hash map to a case class that is easier to deal with
  def toASGEvent(event: java.util.HashMap[String, Object]): Either[FailureReason, ASGEvent] = {
    for {
      eventId <- getValue(event, "id")
      ec2InstanceId <- getValue2(event, "detail", "EC2InstanceId")
      lifecycleHookName <- getValue2(event, "detail", "LifecycleHookName")
      autoScalingGroupName <- getValue2(event, "detail", "AutoScalingGroupName")
    }
    yield {
      ASGEvent(eventId, Detail(ec2InstanceId, lifecycleHookName, autoScalingGroupName))
    }
  }

  // Helper function to retrieve a value from the json hash map or return an error message
  def getValue(map: java.util.HashMap[String, Object], key: String): Either[FailureReason, String] =
    Option(map.get(key))
      .map(_.toString)
      .toRight(s"Missing key $key")

  // Helper function to retrieve a value two levels deep from the json hash map or return an error message
  def getValue2(map: java.util.HashMap[String, Object], key: String, key2: String): Either[FailureReason, String] =
    Option(map.get(key))
      .map(_.asInstanceOf[java.util.HashMap[String, Object]].get(key2))
      .map(_.toString)
      .toRight(s"Missing key $key / $key2")

  // This is the handleRequest function we would have preferred to have to implement. The above code massages
  // the hashmap event object into a more scala friendly event object that can be passed into this simpler
  // handleRequest signature
  def handleRequest(event: ASGEvent, context: Context): Try[String] =
    for {
      (subnetId, roleTag) <- getInstanceDetails(event.detail.ec2InstanceId)
      interfaceId <- getInterfaceId(subnetId, roleTag)
      attachmentId <- attachInterface(event.detail.ec2InstanceId, interfaceId)
    }
    yield attachmentId

  // Retrieve some metadata for the ec2 instance that is starting up. In this case the subnet id the instance is
  // starting in and the value of the roletag associated with this instance.
  def getInstanceDetails(instanceId: String): Try[(String, String)] = {
    Try {
      val instanceDesc = ec2Client
        .describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId))
        .getReservations.get(0)
        .getInstances.get(0)

      val subnetId = instanceDesc.getSubnetId
      val roleTag = instanceDesc.getTags.asScala.filter(_.getKey() == "role").headOption

      if (roleTag.isEmpty)
        throw new RuntimeException(s"No role tag present on ec2 Instance")

      (subnetId, roleTag.get.getValue)
    }
  }

  // There should be an available network interface in the subnet where the ec2 instance is starting that was
  // previously attached to the ec2 instances this new one is replacing. Find the id for this interface.
  def getInterfaceId(subnetId: String, roleTag: String): Try[String] = {
    Try {
      val networkInterfaceIds = ec2Client
        .describeNetworkInterfaces(new DescribeNetworkInterfacesRequest())
        .getNetworkInterfaces
        .asScala
        .filter(_.getSubnetId == subnetId)
        .filter(_.getStatus == "available")
        .filter(_.getTagSet.asScala.filter(_.getKey == "role").head.getValue == roleTag)
        .map(_.getNetworkInterfaceId)

      if (networkInterfaceIds.size == 0 )
        throw new RuntimeException(s"No unattached interfaces for role: ${roleTag}")

      if (networkInterfaceIds.size > 1)
        throw new RuntimeException(s"More than one unattached interface for role: ${roleTag}")

      networkInterfaceIds.head
    }
  }

  // Performs the attachement of the existing network interface to the newly started ec2 instance
  def attachInterface(instanceId: String, networkInterfaceId: String): Try[String] = {
    Try {
      val result: AttachNetworkInterfaceResult = ec2Client.attachNetworkInterface(
        new AttachNetworkInterfaceRequest()
          .withDeviceIndex(1)
          .withInstanceId(instanceId)
          .withNetworkInterfaceId(networkInterfaceId)
      )

      result.getAttachmentId
    }
  }

  // This lambda will be run as part of an autoscaling group lifecycle hook. We need to mark the lifecycle hook as
  // complete so that that autoscaling group is aware that startup is proceeding correctly.
  def completeLifecycleAction(asgEvent: ASGEvent, success: Boolean): Try[String] =
    Try {
      asgClient.completeLifecycleAction(
        new CompleteLifecycleActionRequest()
          .withLifecycleHookName(asgEvent.detail.lifecycleHookName)
          .withAutoScalingGroupName(asgEvent.detail.autoScalingGroupName)
          .withInstanceId(asgEvent.detail.ec2InstanceId)
          .withLifecycleActionResult(if (success) "CONTINUE" else "ABANDON")
      )
    }.map(_ => "Done")
}
