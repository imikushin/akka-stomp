organization := "com.heromq"

name := "akka-stomp"

version := "0.3"

scalaVersion := "2.9.1"

resolvers += "Local M2 repo" at "https://dl.dropbox.com/u/24364253/repo/local-m2-Kmn9rtaxg/"

resolvers += Resolver.url(
  "Local repository", url("https://dl.dropbox.com/u/24364253/repo/local-IHRmxcxtp7/")
)(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "0.6.10",
  "com.typesafe.akka" % "akka-actor" % "2.0.4",
  "com.typesafe.akka" % "akka-slf4j" % "2.0.4",
  "com.typesafe.akka" % "akka-testkit" % "2.0.4" % "test",
  "org.fusesource.stompjms" % "stompjms-client" % "1.13",
  "org.apache.geronimo.specs" % "geronimo-jms_1.1_spec" % "1.1"
)
