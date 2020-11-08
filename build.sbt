name := "kda-flink-efo-consumer"

organization := "example"

version := "0.1"

ThisBuild / scalaVersion := "2.11.12"

val flinkVersion = "1.8.2"

val commonDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "com.amazonaws" % "aws-kinesisanalytics-runtime" % "1.1.0",
  "software.amazon.kinesis" % "amazon-kinesis-connector-flink" % "1.0.1"
    exclude("com.fasterxml.jackson.core", "jackson-databind") exclude("com.google.guava", "guava"),
  "com.google.guava" % "guava" % "18.0",
  "com.twitter" % "chill-protobuf" % "0.7.6" exclude("com.esotericsoftware.kryo", "kyro"),
  "com.google.protobuf" % "protobuf-java" % "3.12.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

scalacOptions += "-target:jvm-1.8"

enablePlugins(ProtobufPlugin)

lazy val flinkEfoConsumer = (project in file("."))
  .settings(
    libraryDependencies := commonDependencies,
    assembly / mainClass := Some("example.KinesisStreamJob")
  )


lazy val flinkEfoConsumerLocal: Project =
  project
    .in(file("flinkEfoConsumer"))
    .dependsOn(RootProject(file(".")))
    .settings(
      Compile / run / mainClass := Some("example.KinesisStreamJob"),
      libraryDependencies ++= commonDependencies.map { module =>
        module.configurations match {
          case Some("provided") => module.withConfigurations(None)
          case _ => module
        }
      }
    )

Compile / run / fork := true
Global / cancelable := true

assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case "Environment.local.json" => MergeStrategy.discard
  case _ => MergeStrategy.first
}
