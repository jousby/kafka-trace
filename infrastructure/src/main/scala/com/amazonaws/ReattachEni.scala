package com.amazonaws


import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  *
  */
class ReattachEni extends RequestHandler[java.util.HashMap[String, Object], String] {

  import AWSMapHelper._

  val ec2Client = AmazonEC2ClientBuilder.defaultClient()

  override def handleRequest(event: java.util.HashMap[String, Object], context: Context): String = {
    val asgEvent = mapToASGEvent(event)

    val result: Option[Try[String]] = asgEvent.map(ae => {
      handleRequest(ae, context)
    })


  }

  def handleRequest(event: ASGEvent, context: Context): Try[String] = {
    // Retrieve the id of the interface we need to attach to our newly restarted instance
    val result = for {
      (subnetId, roleTag) <- getInstanceDetails(event.detail.ec2InstanceId)
      interfaceId <- getInterfaceId(subnetId, roleTag)
    }
    yield {
      attachInterface(event.detail.ec2InstanceId, interfaceId)
    }

    result.flatten
  }

  def getInstanceDetails(instanceId: String): Try[(String, String)] = {
    Try {
      val instanceDesc = ec2Client
        .describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId))
        .getReservations.get(0)
        .getInstances.get(0)

      val subnetId = instanceDesc.getSubnetId
      val roleTag = instanceDesc.getTags.asScala.filter(_.getKey() == "role").head

      (subnetId, roleTag.getValue)
    }
  }

  def getInterfaceId(subnetId: String, roleTag: String): Try[String] = {
    Try {
      val networkInterfaceIds = ec2Client
        .describeNetworkInterfaces(new DescribeNetworkInterfacesRequest())
        .getNetworkInterfaces
        .asScala
        .filter(_.getSubnetId == subnetId)
        .filter(_.getStatus == "UNATTACHED")
        .filter(_.getTagSet.asScala.filter(_.getKey == "role").head.getValue == roleTag)
        .map(_.getNetworkInterfaceId)

      if (networkInterfaceIds.size == 0 )
        throw new RuntimeException(s"No unattached interfaces for role: ${roleTag}")

      if (networkInterfaceIds.size > 1)
        throw new RuntimeException(s"More than one unattached interface for role: ${roleTag}")

      networkInterfaceIds.head
    }
  }

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
}

/**
  * Convert following json that is represented as a java.util.HashMap[String, Object] to sensible case class
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
case class Detail (
  autoScalingGroupName: String,
  lifecycleHookName: String,
  ec2InstanceId: String
)

case class ASGEvent(
  detail: Detail
)

object AWSMapHelper {

  def mapToASGEvent(event: java.util.HashMap[String, Object]): Option[ASGEvent] = {
    // check we have the right event type
    val asgEvent = getString(event, "detail-type")
      .filter(_ == "EC2 Instance-launch Lifecycle Action")
      .map { _ =>
        val ec2InstanceId = getString2(event, "detail", "EC2InstanceId")
        val lifecycleHookName = getString2(event, "detail", "LifecycleHookName")
        val autoScalingGroupName = getString2(event, "detail", "AutoScalingGroupName")

        ASGEvent(
          Detail(
            autoScalingGroupName.getOrElse(throw new RuntimeException("ASGName not present in event struct")),
            lifecycleHookName.getOrElse(throw new RuntimeException("LifecycleHookName not present in event struct")),
            ec2InstanceId.getOrElse(throw new RuntimeException("Ec2InstanceId not present in event struct"))
          )
        )
      }

    asgEvent
  }

  def getString(map: java.util.HashMap[String, Object], key: String): Option[String] = {
    Option(map.get(key))
      .map(_.toString)
  }

  def getString2(map: java.util.HashMap[String, Object], key: String, key2: String): Option[String] = {
    Option(map.get(key))
      .map(_.asInstanceOf[java.util.HashMap[String, Object]].get(key2))
      .map(_.toString)

  }
}

//import boto3
//import botocore
//from datetime import datetime
//
//ec2_client = boto3.client('ec2')
//asg_client = boto3.client('autoscaling')
//
//
//def lambda_handler(event, context):
//    if event["detail-type"] == "EC2 Instance-launch Lifecycle Action":
//        instance_id = event['detail']['EC2InstanceId']
//        LifecycleHookName=event['detail']['LifecycleHookName']
//        AutoScalingGroupName=event['detail']['AutoScalingGroupName']
//        subnet_id = get_subnet_id(instance_id)
//        interface_id = create_interface(subnet_id)
//        attachment = attach_interface(interface_id, instance_id)
//        if not interface_id:
//            complete_lifecycle_action_failure(LifecycleHookName,AutoScalingGroupName,instance_id)
//        elif not attachment:
//            complete_lifecycle_action_failure(LifecycleHookName,AutoScalingGroupName,instance_id)
//            delete_interface(interface_id)
//        else:
//            complete_lifecycle_action_success(LifecycleHookName,AutoScalingGroupName,instance_id)
//
//
//def get_subnet_id(instance_id):
//    try:
//        result = ec2_client.describe_instances(InstanceIds=[instance_id])
//        vpc_subnet_id = result['Reservations'][0]['Instances'][0]['SubnetId']
//        log("Subnet id: {}".format(vpc_subnet_id))
//
//    except botocore.exceptions.ClientError as e:
//        log("Error describing the instance {}: {}".format(instance_id, e.response['Error']))
//        vpc_subnet_id = None
//
//    return vpc_subnet_id
//
//
//def create_interface(subnet_id):
//    network_interface_id = None
//
//    if subnet_id:
//        try:
//            network_interface = ec2_client.create_network_interface(SubnetId=subnet_id)
//            network_interface_id = network_interface['NetworkInterface']['NetworkInterfaceId']
//            log("Created network interface: {}".format(network_interface_id))
//        except botocore.exceptions.ClientError as e:
//            log("Error creating network interface: {}".format(e.response['Error']))
//
//    return network_interface_id
//
//
//def attach_interface(network_interface_id, instance_id):
//    attachment = None
//
//    if network_interface_id and instance_id:
//        try:
//            attach_interface = ec2_client.attach_network_interface(
//                NetworkInterfaceId=network_interface_id,
//                InstanceId=instance_id,
//                DeviceIndex=1
//            )
//            attachment = attach_interface['AttachmentId']
//            log("Created network attachment: {}".format(attachment))
//        except botocore.exceptions.ClientError as e:
//            log("Error attaching network interface: {}".format(e.response['Error']))
//
//    return attachment
//
//
//def delete_interface(network_interface_id):
//    try:
//        ec2_client.delete_network_interface(
//            NetworkInterfaceId=network_interface_id
//        )
//        log("Deleted network interface: {}".format(network_interface_id))
//        return True
//
//    except botocore.exceptions.ClientError as e:
//        log("Error deleting interface {}: {}".format(network_interface_id, e.response['Error']))
//
//
//def complete_lifecycle_action_success(hookname,groupname,instance_id):
//    try:
//        asg_client.complete_lifecycle_action(
//                LifecycleHookName=hookname,
//                AutoScalingGroupName=groupname,
//                InstanceId=instance_id,
//                LifecycleActionResult='CONTINUE'
//            )
//        log("Lifecycle hook CONTINUEd for: {}".format(instance_id))
//    except botocore.exceptions.ClientError as e:
//            log("Error completing life cycle hook for instance {}: {}".format(instance_id, e.response['Error']))
//            log('{"Error": "1"}')
//
//def complete_lifecycle_action_failure(hookname,groupname,instance_id):
//    try:
//        asg_client.complete_lifecycle_action(
//                LifecycleHookName=hookname,
//                AutoScalingGroupName=groupname,
//                InstanceId=instance_id,
//                LifecycleActionResult='ABANDON'
//            )
//        log("Lifecycle hook ABANDONed for: {}".format(instance_id))
//    except botocore.exceptions.ClientError as e:
//            log("Error completing life cycle hook for instance {}: {}".format(instance_id, e.response['Error']))
//            log('{"Error": "1"}')
//
//
//def log(error):
//    print('{}Z {}'.format(datetime.utcnow().isoformat(), error))