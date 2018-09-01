import sbt._

object Resolvers {
  val cakeRepo = Resolver.bintrayRepo("cakesolutions", "maven")
  val confluentRepo = "confluent" at "http://packages.confluent.io/maven/"
}
