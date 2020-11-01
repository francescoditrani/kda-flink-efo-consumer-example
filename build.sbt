ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "kda-flink-efo-consumer"

version := "0.1"

scalaVersion := "2.11.12"

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

lazy val flinkStream = (project in file("."))
  .settings(
    libraryDependencies := commonDependencies,
    assembly / mainClass := Some("org.ree.StreamPredictionJob")
  )


lazy val flinkStreamLocal: Project =
  project
    .in(file("flinkStreamLocal"))
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

assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case "Environment.local.json" => MergeStrategy.discard
  case _ => MergeStrategy.first
}
