import sbt._

object Dependencies {

  // Dependency versions
  lazy val akkaVersion = "2.5.11"
  lazy val avro4sVersion = "2.0.3"
  lazy val awsLambdaJava = "1.2.0"
  lazy val awsSdkJavaVersion = "1.11.292"
  lazy val awsSdkJavaV2Version = "2.0.6"
  lazy val flinkVersion = "1.7.1"
  lazy val kafkaAvroSerializerVersion = "3.3.2"
  lazy val logbackVersion = "1.2.3"
  lazy val scallopVersion = "3.1.1"
  lazy val scalaKafkaClientVersion = "1.1.1"
  lazy val scalaMockVersion = "4.1.0"
  lazy val scalaLoggingVersion = "3.9.0"
  lazy val scalaTestVersion = "3.0.5"


  // Avro4s
  lazy val avro4s = "com.sksamuel.avro4s" %% "avro4s-core" % avro4sVersion

  // Flink
  lazy val flink = "org.apache.flink" %% "flink-scala" % flinkVersion
  lazy val flinkStreaming = "org.apache.flink" %% "flink-streaming-scala" % flinkVersion
  lazy val flinkKafkaConnector = "org.apache.flink" %% "flink-connector-kafka-0.11" % flinkVersion
  lazy val flinkAvroConfluentRegistry = "org.apache.flink" % "flink-avro-confluent-registry" % flinkVersion

  // Akka
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion

  // AWS Lambda
  lazy val awsLambdaJavaCore = "com.amazonaws" % "aws-lambda-java-core" % awsLambdaJava
  lazy val awsLambdaJavaEvents = "com.amazonaws" % "aws-lambda-java-events" % awsLambdaJava

  // AWS SDK Java V2 (Developer Preview)
  lazy val awsSdkJavaV2 = "software.amazon.awssdk" % "aws-sdk-java" % awsSdkJavaV2Version

  // AWS SDK Java V1 All Inclusive
  lazy val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % awsSdkJavaVersion

  // AWS SDK JAVA V1 Individual Libraries
  lazy val awsAcm = "com.amazonaws" % "aws-java-sdk-acm" % awsSdkJavaVersion
  lazy val awsApiGateway = "com.amazonaws" % "aws-java-sdk-api-gateway" % awsSdkJavaVersion
  lazy val awsApplicationAutoScaling = "com.amazonaws" % "aws-java-sdk-applicationautoscaling" % awsSdkJavaVersion
  lazy val awsAppstream = "com.amazonaws" % "aws-java-sdk-appstream" % awsSdkJavaVersion
  lazy val awsAthena = "com.amazonaws" % "aws-java-sdk-athena" % awsSdkJavaVersion
  lazy val awsAutoscaling = "com.amazonaws" % "aws-java-sdk-autoscaling" % awsSdkJavaVersion
  lazy val awsBatch = "com.amazonaws" % "aws-java-sdk-batch" % awsSdkJavaVersion
  lazy val awsBudgets = "com.amazonaws" % "aws-java-sdk-budgets" % awsSdkJavaVersion
  lazy val awsCloudDirectory = "com.amazonaws" % "aws-java-sdk-clouddirectory" % awsSdkJavaVersion
  lazy val awsCloudFormation = "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkJavaVersion
  lazy val awsCloudFront = "com.amazonaws" % "aws-java-sdk-cloudfront" % awsSdkJavaVersion
  lazy val awsCloudHsm = "com.amazonaws" % "aws-java-sdk-cloudhsm" % awsSdkJavaVersion
  lazy val awsCloudHsmV2 = "com.amazonaws" % "aws-java-sdk-cloudhsmv2" % awsSdkJavaVersion
  lazy val awsCloudSearch = "com.amazonaws" % "aws-java-sdk-cloudsearch" % awsSdkJavaVersion
  lazy val awsCloudTrail = "com.amazonaws" % "aws-java-sdk-cloudtrail" % awsSdkJavaVersion
  lazy val awsCloudWatch = "com.amazonaws" % "aws-java-sdk-cloudwatch" % awsSdkJavaVersion
  lazy val awsCloudWatchMetrics = "com.amazonaws" % "aws-java-sdk-cloudwatchmetrics" % awsSdkJavaVersion
  lazy val awsCodeBuild = "com.amazonaws" % "aws-java-sdk-codebuild" % awsSdkJavaVersion
  lazy val awsCodeDeploy = "com.amazonaws" % "aws-java-sdk-codedeploy" % awsSdkJavaVersion
  lazy val awsCodeCommit = "com.amazonaws" % "aws-java-sdk-codecommit" % awsSdkJavaVersion
  lazy val awsCodeGenerator = "com.amazonaws" % "aws-java-sdk-code-generator" % awsSdkJavaVersion
  lazy val awsCodegenMavenPlugin = "com.amazonaws" % "aws-java-sdk-codegen-maven-plugin" % awsSdkJavaVersion
  lazy val awsCodepipeline = "com.amazonaws" % "aws-java-sdk-codepipeline" % awsSdkJavaVersion
  lazy val awsCodestar = "com.amazonaws" % "aws-java-sdk-codestar" % awsSdkJavaVersion
  lazy val awsCognitoIdentity = "com.amazonaws" % "aws-java-sdk-cognitoidentity" % awsSdkJavaVersion
  lazy val awsCognitoIdp = "com.amazonaws" % "aws-java-sdk-cognitoidp" % awsSdkJavaVersion
  lazy val awsCognitoSync = "com.amazonaws" % "aws-java-sdk-cognitosync" % awsSdkJavaVersion
  lazy val awsConfig = "com.amazonaws" % "aws-java-sdk-config" % awsSdkJavaVersion
  lazy val awsCore = "com.amazonaws" % "aws-java-sdk-core" % awsSdkJavaVersion
  lazy val awsCostAndUsageReport = "com.amazonaws" % "aws-java-sdk-costandusagereport" % awsSdkJavaVersion
  lazy val awsDatapipeline = "com.amazonaws" % "aws-java-sdk-datapipeline" % awsSdkJavaVersion
  lazy val awsDax = "com.amazonaws" % "aws-java-sdk-dax" % awsSdkJavaVersion
  lazy val awsDeviceFarm = "com.amazonaws" % "aws-java-sdk-devicefarm" % awsSdkJavaVersion
  lazy val awsDirectConnect = "com.amazonaws" % "aws-java-sdk-directconnect" % awsSdkJavaVersion
  lazy val awsDirectory = "com.amazonaws" % "aws-java-sdk-directory" % awsSdkJavaVersion
  lazy val awsDiscovery = "com.amazonaws" % "aws-java-sdk-discovery" % awsSdkJavaVersion
  lazy val awsDms = "com.amazonaws" % "aws-java-sdk-dms" % awsSdkJavaVersion
  lazy val awsDynamoDb = "com.amazonaws" % "aws-java-sdk-dynamodb" % awsSdkJavaVersion
  lazy val awsEc2 = "com.amazonaws" % "aws-java-sdk-ec2" % awsSdkJavaVersion
  lazy val awsEcr = "com.amazonaws" % "aws-java-sdk-ecr" % awsSdkJavaVersion
  lazy val awsEcs = "com.amazonaws" % "aws-java-sdk-ecs" % awsSdkJavaVersion
  lazy val awsEfs = "com.amazonaws" % "aws-java-sdk-efs" % awsSdkJavaVersion
  lazy val awsElasticBeanstalk = "com.amazonaws" % "aws-java-sdk-elasticbeanstalk" % awsSdkJavaVersion
  lazy val awsElasticLoadBalancing = "com.amazonaws" % "aws-java-sdk-elasticloadbalancing" % awsSdkJavaVersion
  lazy val awsElasticLoadbalancingV2 = "com.amazonaws" % "aws-java-sdk-elasticloadbalancingv2" % awsSdkJavaVersion
  lazy val awsElasticTranscoder = "com.amazonaws" % "aws-java-sdk-elastictranscoder" % awsSdkJavaVersion
  lazy val awsElasticcache = "com.amazonaws" % "aws-java-sdk-elasticache" % awsSdkJavaVersion
  lazy val awsElasticsearch = "com.amazonaws" % "aws-java-sdk-elasticsearch" % awsSdkJavaVersion
  lazy val awsEmr = "com.amazonaws" % "aws-java-sdk-emr" % awsSdkJavaVersion
  lazy val awsEvents = "com.amazonaws" % "aws-java-sdk-events" % awsSdkJavaVersion
  lazy val awsGamelift = "com.amazonaws" % "aws-java-sdk-gamelift" % awsSdkJavaVersion
  lazy val awsGlacier = "com.amazonaws" % "aws-java-sdk-glacier" % awsSdkJavaVersion
  lazy val awsGlue = "com.amazonaws" % "aws-java-sdk-glue" % awsSdkJavaVersion
  lazy val awsGreengrass = "com.amazonaws" % "aws-java-sdk-greengrass" % awsSdkJavaVersion
  lazy val awsHealth = "com.amazonaws" % "aws-java-sdk-health" % awsSdkJavaVersion
  lazy val awsIam = "com.amazonaws" % "aws-java-sdk-iam" % awsSdkJavaVersion
  lazy val awsImportExport = "com.amazonaws" % "aws-java-sdk-importexport" % awsSdkJavaVersion
  lazy val awsInspector = "com.amazonaws" % "aws-java-sdk-inspector" % awsSdkJavaVersion
  lazy val awsIot = "com.amazonaws" % "aws-java-sdk-iot" % awsSdkJavaVersion
  lazy val awsKinesis = "com.amazonaws" % "aws-java-sdk-kinesis" % awsSdkJavaVersion
  lazy val awsKms = "com.amazonaws" % "aws-java-sdk-kms" % awsSdkJavaVersion
  lazy val awsLambda = "com.amazonaws" % "aws-java-sdk-lambda" % awsSdkJavaVersion
  lazy val awsLex = "com.amazonaws" % "aws-java-sdk-lex" % awsSdkJavaVersion
  lazy val awsLexModelBuilding = "com.amazonaws" % "aws-java-sdk-lexmodelbuilding" % awsSdkJavaVersion
  lazy val awsLightSail = "com.amazonaws" % "aws-java-sdk-lightsail" % awsSdkJavaVersion
  lazy val awsLogs = "com.amazonaws" % "aws-java-sdk-logs" % awsSdkJavaVersion
  lazy val awsMachineLearning = "com.amazonaws" % "aws-java-sdk-machinelearning" % awsSdkJavaVersion
  lazy val awsMarketplaceCommerceAnalytics = "com.amazonaws" % "aws-java-sdk-marketplacecommerceanalytics" % awsSdkJavaVersion
  lazy val awsMarketplaceEntitlement = "com.amazonaws" % "aws-java-sdk-marketplaceentitlement" % awsSdkJavaVersion
  lazy val awsMarketplaceMeteringService = "com.amazonaws" % "aws-java-sdk-marketplacemeteringservice" % awsSdkJavaVersion
  lazy val awsMechanicalTurkRequester = "com.amazonaws" % "aws-java-sdk-mechanicalturkrequester" % awsSdkJavaVersion
  lazy val awsMigrationHub = "com.amazonaws" % "aws-java-sdk-migrationhub" % awsSdkJavaVersion
  lazy val awsMobile = "com.amazonaws" % "aws-java-sdk-mobile" % awsSdkJavaVersion
  lazy val awsModels = "com.amazonaws" % "aws-java-sdk-models" % awsSdkJavaVersion
  lazy val awsOpenSdk = "com.amazonaws" % "aws-java-sdk-opensdk" % awsSdkJavaVersion
  lazy val awsOpsworks = "com.amazonaws" % "aws-java-sdk-opsworks" % awsSdkJavaVersion
  lazy val awsOpsworksScm = "com.amazonaws" % "aws-java-sdk-opsworkscm" % awsSdkJavaVersion
  lazy val awsOrganizations = "com.amazonaws" % "aws-java-sdk-organizations" % awsSdkJavaVersion
  lazy val awsOsgi = "com.amazonaws" % "aws-java-sdk-osgi" % awsSdkJavaVersion
  lazy val awsPinpoint = "com.amazonaws" % "aws-java-sdk-pinpoint" % awsSdkJavaVersion
  lazy val awsPolly = "com.amazonaws" % "aws-java-sdk-polly" % awsSdkJavaVersion
  lazy val awsPricing = "com.amazonaws" % "aws-java-sdk-pricing" % awsSdkJavaVersion
  lazy val awsRds = "com.amazonaws" % "aws-java-sdk-rds" % awsSdkJavaVersion
  lazy val awsRedshift = "com.amazonaws" % "aws-java-sdk-redshift" % awsSdkJavaVersion
  lazy val awsRekognition = "com.amazonaws" % "aws-java-sdk-rekognition" % awsSdkJavaVersion
  lazy val awsResourceGroupsTaggingApi = "com.amazonaws" % "aws-java-sdk-resourcegroupstaggingapi" % awsSdkJavaVersion
  lazy val awsRoute53 = "com.amazonaws" % "aws-java-sdk-route53" % awsSdkJavaVersion
  lazy val awsS3 = "com.amazonaws" % "aws-java-sdk-s3" % awsSdkJavaVersion
  lazy val awsServerMigration = "com.amazonaws" % "aws-java-sdk-servermigration" % awsSdkJavaVersion
  lazy val awsServiceCatalog = "com.amazonaws" % "aws-java-sdk-servicecatalog" % awsSdkJavaVersion
  lazy val awsSes = "com.amazonaws" % "aws-java-sdk-ses" % awsSdkJavaVersion
  lazy val awsShield = "com.amazonaws" % "aws-java-sdk-shield" % awsSdkJavaVersion
  lazy val awsSimpleDb = "com.amazonaws" % "aws-java-sdk-simpledb" % awsSdkJavaVersion
  lazy val awsSimpleWorkflow = "com.amazonaws" % "aws-java-sdk-simpleworkflow" % awsSdkJavaVersion
  lazy val awsSnowball = "com.amazonaws" % "aws-java-sdk-snowball" % awsSdkJavaVersion
  lazy val awsSns = "com.amazonaws" % "aws-java-sdk-sns" % awsSdkJavaVersion
  lazy val awsSqs = "com.amazonaws" % "aws-java-sdk-sqs" % awsSdkJavaVersion
  lazy val awsSsm = "com.amazonaws" % "aws-java-sdk-ssm" % awsSdkJavaVersion
  lazy val awsStepFunctions = "com.amazonaws" % "aws-java-sdk-stepfunctions" % awsSdkJavaVersion
  lazy val awsStorageGateway = "com.amazonaws" % "aws-java-sdk-storagegateway" % awsSdkJavaVersion
  lazy val awsSts = "com.amazonaws" % "aws-java-sdk-sts" % awsSdkJavaVersion
  lazy val awsSupport = "com.amazonaws" % "aws-java-sdk-support" % awsSdkJavaVersion
  lazy val awsTestUtils = "com.amazonaws" % "aws-java-sdk-test-utils" % awsSdkJavaVersion
  lazy val awsWaf = "com.amazonaws" % "aws-java-sdk-waf" % awsSdkJavaVersion
  lazy val awsWorkdocs = "com.amazonaws" % "aws-java-sdk-workdocs" % awsSdkJavaVersion
  lazy val awsWorkspaces = "com.amazonaws" % "aws-java-sdk-workspaces" % awsSdkJavaVersion
  lazy val awsXray = "com.amazonaws" % "aws-java-sdk-xray" % awsSdkJavaVersion

  // Logback
  lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion

  // Kafka Avro Serializer
  lazy val kafkaAvroSerializer = "io.confluent" % "kafka-avro-serializer" % kafkaAvroSerializerVersion

  // Scala CLI Args
  lazy val scallop = "org.rogach" %% "scallop" % scallopVersion

  // Scala Logging
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion

  // Scala Kafka Client
  lazy val scalaKafkaClient = "net.cakesolutions" %% "scala-kafka-client" % scalaKafkaClientVersion

  // Scala Mock
  lazy val scalaMock = "org.scalamock" %% "scalamock" % scalaMockVersion

  // Scalatest
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
}
