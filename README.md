# KDA Flink EFO consumer example

A small example of a Flink Job to be released as a Kinesis Data Analytics application. 
In Scala. Using protobuf serdes and Kinesis EFO consumer.

This example is inspired by the AWS ones under:

https://github.com/aws-samples/amazon-kinesis-data-analytics-java-examples

in particular the "GettingStarted" and "EfoConsumer" ones.

### The AWS example (what I don't like)

- Java;
- Maven builder (`pom.xml`);
- String serdes for Kinesis consumer/producer;
- no explanation on how to properly run a project locally.

### This example

- Scala;
- Sbt builder and assembly plugin;
- shows a way to implement custom (protobuf) Kinesis serdes; 
- shows how to run the project locally, with Scala-compatible environment loading.

#### Protobuf serdes

This example is using the [sbt-protobuf](`https://github.com/sbt/sbt-protobuf`) plugin to generate Java classes from the `proto` schema definitions in `main/protobuf`.

These classes provide methods for serializing from/to byte arrays, that are used in the `serdes` implementation.

A protobuf Kryo serializer for Flink it's then registered using [Chill]([https://github.com/twitter/chill/tree/master) with:

```
env.registerTypeWithKryoSerializer(classOf[Person], classOf[ProtobufSerializer])
```
 
#### Environment loading

Kinesis Data Analytics requires environment properties in the [CreateApplication](https://docs.aws.amazon.com/kinesisanalytics/latest/apiv2/API_CreateApplication.html) and [UpdateApplication](https://docs.aws.amazon.com/kinesisanalytics/latest/apiv2/API_UpdateApplication.html) actions, under `EnvironmentProperties` and `EnvironmentPropertiesUpdate` respectively.

To load this environment while running on the cluster, it's enough to just call:

```
KinesisAnalyticsRuntime.getApplicationProperties()
``` 

To load environment properties from a file in the `resource` folder should be enough (spoiler, doesn't work) to call:

```
KinesisAnalyticsRuntime.getApplicationProperties(String filename)
```

but in Scala this doesn't work, since the Kinesis library is not using `getClass.getResourceAsStream(String filename)`. 

 => `config/EnvironmentLoader` shows how to achieve environment loading from KDA runtime, with a fallback to an environment file.

#### Running the project locally

The [AWS documentation](https://docs.aws.amazon.com/kinesisanalytics/latest/java/how-creating-apps.html) asks to compile the Apache Flink Kinesis connector; this is no more necessary with the [new EFO compatible connector](https://github.com/awslabs/amazon-kinesis-connector-flink).

The `flink-scala` and `flink-streaming-scala` libraries need to be marked as "provided"; this means that, if we run the project locally, we are not going to find their classes in the classpath.

To have this library loaded locally, run:

```
sbt clean  "project flinkEfoConsumerLocal" run
```

this project extends the base one, removing the `provided` configuration from the dependencies.

#### Create the fat Jar

This example uses the `sbt-assembly` plugin; just run:

```
sbt clean assembly
```
