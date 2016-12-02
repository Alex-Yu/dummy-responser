
organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.14"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"                    %%  "spray-can"       % sprayV,
    "io.spray"                    %%  "spray-routing"   % sprayV,
    "io.spray"                    %%  "spray-testkit"   % sprayV  % "test",
    "com.typesafe.akka"           %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"           %%  "akka-testkit"    % akkaV   % "test",
    "org.specs2"                  %%  "specs2-core"     % "2.3.11" % "test",
    "ch.qos.logback"              %  "logback-classic"  % "1.1.7",
    "com.typesafe.scala-logging"  %% "scala-logging"    % "3.4.0"
  )
}

assemblyJarName in assembly := "dummy-bidder.jar"

javaOptions in run ++= Seq(
  "-Dcom.sun.management.jmxremote.port=1032",
  "-Dcom.sun.management.jmxremote.authenticate=false",
  "-Dcom.sun.management.jmxremote.ssl=false"
)

Revolver.settings
