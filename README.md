# KDA Flink EFO consumer example

A small example of a Flink Job to be released as a Kinesis Data Analytics application. 
In Scala. Using protobuf serdes and Kinesis EFO consumer.

This example is inspired by the AWS ones under:

https://github.com/aws-samples/amazon-kinesis-data-analytics-java-examples

in particular the "GettingStarted" and "EfoConsumer" ones.

### The AWS example (what I don't like)

- All the AWS examples are written in Java and use `pom.xml` file for building the project with Maven;
- Kinesis consumers and producers are only using String serdes;
- Explanation on how to properly run a project locally looks out of the scope.

### This example

- This example is written in Scala and uses `build.sbt` for building the project;
- shows a way to implement custom (protobuf) Kinesis serdes; 
- shows how to properly load the environment and how to run the project locally (without any needs to compile/run a Flink cluster locally).

