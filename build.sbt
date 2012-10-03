organization := "com.heromq"

name := "heromq-akka"

version := "0.1"

scalaVersion := "2.9.1"

resolvers += "Local M2 repo" at "http://dl.dropbox.com/u/24364253/repo/local-m2-Kmn9rtaxg/"

resolvers += Resolver.url(
  "Local repository", url("http://dl.dropbox.com/u/24364253/repo/local-IHRmxcxtp7/")
)(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "0.6.9",
  "com.typesafe.akka" % "akka-actor" % "2.0.3",
  "com.typesafe.akka" % "akka-slf4j" % "2.0.3",
  "com.typesafe.akka" % "akka-testkit" % "2.0.3" % "test",
  "org.fusesource.stompjms" % "stompjms-client" % "1.13",
  "org.apache.geronimo.specs" % "geronimo-jms_1.1_spec" % "1.1"
)
