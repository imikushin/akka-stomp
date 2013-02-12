organization := "com.heromq"

name := "akka-stomp"

version := "0.4"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.0.1",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test",
  "org.fusesource.stompjms" % "stompjms-client" % "1.13",
  "org.apache.geronimo.specs" % "geronimo-jms_1.1_spec" % "1.1"
)
