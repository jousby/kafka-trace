import sbt._

object Dependencies {

  // Dependency versions
  lazy val akkaVersion = "2.5.11"
  lazy val avro4sVersion = "1.9.0"
  lazy val awsVersion = "1.11.292"
  lazy val flinkVersion = "1.6.0"
  lazy val kafkaAvroSerializerVersion = "4.0.0"
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

  // AWS All Inclusive
  lazy val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % awsVersion

  // AWS Individual Libraries
  lazy val awsAcm = "com.amazonaws" % "aws-java-sdk-acm" % awsVersion
  lazy val awsApiGateway = "com.amazonaws" % "aws-java-sdk-api-gateway" % awsVersion
  lazy val awsApplicationAutoScaling = "com.amazonaws" % "aws-java-sdk-applicationautoscaling" % awsVersion
  lazy val awsAppstream = "com.amazonaws" % "aws-java-sdk-appstream" % awsVersion
  lazy val awsAthena = "com.amazonaws" % "aws-java-sdk-athena" % awsVersion
  lazy val awsAutoscaling = "com.amazonaws" % "aws-java-sdk-autoscaling" % awsVersion
  lazy val awsBatch = "com.amazonaws" % "aws-java-sdk-batch" % awsVersion
  lazy val awsBudgets = "com.amazonaws" % "aws-java-sdk-budgets" % awsVersion
  lazy val awsCloudDirectory = "com.amazonaws" % "aws-java-sdk-clouddirectory" % awsVersion
  lazy val awsCloudFormation = "com.amazonaws" % "aws-java-sdk-cloudformation" % awsVersion
  lazy val awsCloudFront = "com.amazonaws" % "aws-java-sdk-cloudfront" % awsVersion
  lazy val awsCloudHsm = "com.amazonaws" % "aws-java-sdk-cloudhsm" % awsVersion
  lazy val awsCloudHsmV2 = "com.amazonaws" % "aws-java-sdk-cloudhsmv2" % awsVersion
  lazy val awsCloudSearch = "com.amazonaws" % "aws-java-sdk-cloudsearch" % awsVersion
  lazy val awsCloudTrail = "com.amazonaws" % "aws-java-sdk-cloudtrail" % awsVersion
  lazy val awsCloudWatch = "com.amazonaws" % "aws-java-sdk-cloudwatch" % awsVersion
  lazy val awsCloudWatchMetrics = "com.amazonaws" % "aws-java-sdk-cloudwatchmetrics" % awsVersion
  lazy val awsCodeBuild = "com.amazonaws" % "aws-java-sdk-codebuild" % awsVersion
  lazy val awsCodeDeploy = "com.amazonaws" % "aws-java-sdk-codedeploy" % awsVersion
  lazy val awsCodeCommit = "com.amazonaws" % "aws-java-sdk-codecommit" % awsVersion
  lazy val awsCodeGenerator = "com.amazonaws" % "aws-java-sdk-code-generator" % awsVersion
  lazy val awsCodegenMavenPlugin = "com.amazonaws" % "aws-java-sdk-codegen-maven-plugin" % awsVersion
  lazy val awsCodepipeline = "com.amazonaws" % "aws-java-sdk-codepipeline" % awsVersion
  lazy val awsCodestar = "com.amazonaws" % "aws-java-sdk-codestar" % awsVersion
  lazy val awsCognitoIdentity = "com.amazonaws" % "aws-java-sdk-cognitoidentity" % awsVersion
  lazy val awsCognitoIdp = "com.amazonaws" % "aws-java-sdk-cognitoidp" % awsVersion
  lazy val awsCognitoSync = "com.amazonaws" % "aws-java-sdk-cognitosync" % awsVersion
  lazy val awsConfig = "com.amazonaws" % "aws-java-sdk-config" % awsVersion
  lazy val awsCore = "com.amazonaws" % "aws-java-sdk-core" % awsVersion
  lazy val awsCostAndUsageReport = "com.amazonaws" % "aws-java-sdk-costandusagereport" % awsVersion
  lazy val awsDatapipeline = "com.amazonaws" % "aws-java-sdk-datapipeline" % awsVersion
  lazy val awsDax = "com.amazonaws" % "aws-java-sdk-dax" % awsVersion
  lazy val awsDeviceFarm = "com.amazonaws" % "aws-java-sdk-devicefarm" % awsVersion
  lazy val awsDirectConnect = "com.amazonaws" % "aws-java-sdk-directconnect" % awsVersion
  lazy val awsDirectory = "com.amazonaws" % "aws-java-sdk-directory" % awsVersion
  lazy val awsDiscovery = "com.amazonaws" % "aws-java-sdk-discovery" % awsVersion
  lazy val awsDms = "com.amazonaws" % "aws-java-sdk-dms" % awsVersion
  lazy val awsDynamoDb = "com.amazonaws" % "aws-java-sdk-dynamodb" % awsVersion
  lazy val awsEc2 = "com.amazonaws" % "aws-java-sdk-ec2" % awsVersion
  lazy val awsEcr = "com.amazonaws" % "aws-java-sdk-ecr" % awsVersion
  lazy val awsEcs = "com.amazonaws" % "aws-java-sdk-ecs" % awsVersion
  lazy val awsEfs = "com.amazonaws" % "aws-java-sdk-efs" % awsVersion
  lazy val awsElasticBeanstalk = "com.amazonaws" % "aws-java-sdk-elasticbeanstalk" % awsVersion
  lazy val awsElasticLoadBalancing = "com.amazonaws" % "aws-java-sdk-elasticloadbalancing" % awsVersion
  lazy val awsElasticLoadbalancingV2 = "com.amazonaws" % "aws-java-sdk-elasticloadbalancingv2" % awsVersion
  lazy val awsElasticTranscoder = "com.amazonaws" % "aws-java-sdk-elastictranscoder" % awsVersion
  lazy val awsElasticcache = "com.amazonaws" % "aws-java-sdk-elasticache" % awsVersion
  lazy val awsElasticsearch = "com.amazonaws" % "aws-java-sdk-elasticsearch" % awsVersion
  lazy val awsEmr = "com.amazonaws" % "aws-java-sdk-emr" % awsVersion
  lazy val awsEvents = "com.amazonaws" % "aws-java-sdk-events" % awsVersion
  lazy val awsGamelift = "com.amazonaws" % "aws-java-sdk-gamelift" % awsVersion
  lazy val awsGlacier = "com.amazonaws" % "aws-java-sdk-glacier" % awsVersion
  lazy val awsGlue = "com.amazonaws" % "aws-java-sdk-glue" % awsVersion
  lazy val awsGreengrass = "com.amazonaws" % "aws-java-sdk-greengrass" % awsVersion
  lazy val awsHealth = "com.amazonaws" % "aws-java-sdk-health" % awsVersion
  lazy val awsIam = "com.amazonaws" % "aws-java-sdk-iam" % awsVersion
  lazy val awsImportExport = "com.amazonaws" % "aws-java-sdk-importexport" % awsVersion
  lazy val awsInspector = "com.amazonaws" % "aws-java-sdk-inspector" % awsVersion
  lazy val awsIot = "com.amazonaws" % "aws-java-sdk-iot" % awsVersion
  lazy val awsKinesis = "com.amazonaws" % "aws-java-sdk-kinesis" % awsVersion
  lazy val awsKms = "com.amazonaws" % "aws-java-sdk-kms" % awsVersion
  lazy val awsLambda = "com.amazonaws" % "aws-java-sdk-lambda" % awsVersion
  lazy val awsLex = "com.amazonaws" % "aws-java-sdk-lex" % awsVersion
  lazy val awsLexModelBuilding = "com.amazonaws" % "aws-java-sdk-lexmodelbuilding" % awsVersion
  lazy val awsLightSail = "com.amazonaws" % "aws-java-sdk-lightsail" % awsVersion
  lazy val awsLogs = "com.amazonaws" % "aws-java-sdk-logs" % awsVersion
  lazy val awsMachineLearning = "com.amazonaws" % "aws-java-sdk-machinelearning" % awsVersion
  lazy val awsMarketplaceCommerceAnalytics = "com.amazonaws" % "aws-java-sdk-marketplacecommerceanalytics" % awsVersion
  lazy val awsMarketplaceEntitlement = "com.amazonaws" % "aws-java-sdk-marketplaceentitlement" % awsVersion
  lazy val awsMarketplaceMeteringService = "com.amazonaws" % "aws-java-sdk-marketplacemeteringservice" % awsVersion
  lazy val awsMechanicalTurkRequester = "com.amazonaws" % "aws-java-sdk-mechanicalturkrequester" % awsVersion
  lazy val awsMigrationHub = "com.amazonaws" % "aws-java-sdk-migrationhub" % awsVersion
  lazy val awsMobile = "com.amazonaws" % "aws-java-sdk-mobile" % awsVersion
  lazy val awsModels = "com.amazonaws" % "aws-java-sdk-models" % awsVersion
  lazy val awsOpenSdk = "com.amazonaws" % "aws-java-sdk-opensdk" % awsVersion
  lazy val awsOpsworks = "com.amazonaws" % "aws-java-sdk-opsworks" % awsVersion
  lazy val awsOpsworksScm = "com.amazonaws" % "aws-java-sdk-opsworkscm" % awsVersion
  lazy val awsOrganizations = "com.amazonaws" % "aws-java-sdk-organizations" % awsVersion
  lazy val awsOsgi = "com.amazonaws" % "aws-java-sdk-osgi" % awsVersion
  lazy val awsPinpoint = "com.amazonaws" % "aws-java-sdk-pinpoint" % awsVersion
  lazy val awsPolly = "com.amazonaws" % "aws-java-sdk-polly" % awsVersion
  lazy val awsPricing = "com.amazonaws" % "aws-java-sdk-pricing" % awsVersion
  lazy val awsRds = "com.amazonaws" % "aws-java-sdk-rds" % awsVersion
  lazy val awsRedshift = "com.amazonaws" % "aws-java-sdk-redshift" % awsVersion
  lazy val awsRekognition = "com.amazonaws" % "aws-java-sdk-rekognition" % awsVersion
  lazy val awsResourceGroupsTaggingApi = "com.amazonaws" % "aws-java-sdk-resourcegroupstaggingapi" % awsVersion
  lazy val awsRoute53 = "com.amazonaws" % "aws-java-sdk-route53" % awsVersion
  lazy val awsS3 = "com.amazonaws" % "aws-java-sdk-s3" % awsVersion
  lazy val awsServerMigration = "com.amazonaws" % "aws-java-sdk-servermigration" % awsVersion
  lazy val awsServiceCatalog = "com.amazonaws" % "aws-java-sdk-servicecatalog" % awsVersion
  lazy val awsSes = "com.amazonaws" % "aws-java-sdk-ses" % awsVersion
  lazy val awsShield = "com.amazonaws" % "aws-java-sdk-shield" % awsVersion
  lazy val awsSimpleDb = "com.amazonaws" % "aws-java-sdk-simpledb" % awsVersion
  lazy val awsSimpleWorkflow = "com.amazonaws" % "aws-java-sdk-simpleworkflow" % awsVersion
  lazy val awsSnowball = "com.amazonaws" % "aws-java-sdk-snowball" % awsVersion
  lazy val awsSns = "com.amazonaws" % "aws-java-sdk-sns" % awsVersion
  lazy val awsSqs = "com.amazonaws" % "aws-java-sdk-sqs" % awsVersion
  lazy val awsSsm = "com.amazonaws" % "aws-java-sdk-ssm" % awsVersion
  lazy val awsStepFunctions = "com.amazonaws" % "aws-java-sdk-stepfunctions" % awsVersion
  lazy val awsStorageGateway = "com.amazonaws" % "aws-java-sdk-storagegateway" % awsVersion
  lazy val awsSts = "com.amazonaws" % "aws-java-sdk-sts" % awsVersion
  lazy val awsSupport = "com.amazonaws" % "aws-java-sdk-support" % awsVersion
  lazy val awsTestUtils = "com.amazonaws" % "aws-java-sdk-test-utils" % awsVersion
  lazy val awsWaf = "com.amazonaws" % "aws-java-sdk-waf" % awsVersion
  lazy val awsWorkdocs = "com.amazonaws" % "aws-java-sdk-workdocs" % awsVersion
  lazy val awsWorkspaces = "com.amazonaws" % "aws-java-sdk-workspaces" % awsVersion
  lazy val awsXray = "com.amazonaws" % "aws-java-sdk-xray" % awsVersion

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
