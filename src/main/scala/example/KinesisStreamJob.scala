package example

import com.twitter.chill.protobuf.ProtobufSerializer
import com.typesafe.scalalogging.LazyLogging
import example.PersonOuterClass.Person
import example.config.EnvironmentLoader.{consumerProperties, flinkInputStream, producerProperties}
import example.serdes.{ProtoDeserializer, ProtoSerializer}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import software.amazon.kinesis.connectors.flink.{FlinkKinesisConsumer, FlinkKinesisProducer}

object KinesisStreamJob extends LazyLogging {

  logger.info("Starting Flink Job..")

  val defaultPerson: Person = Person.getDefaultInstance

  def kinesisConsumer: FlinkKinesisConsumer[Person] =
    new FlinkKinesisConsumer[Person](
      flinkInputStream,
      new ProtoDeserializer[Person](defaultPerson),
      consumerProperties
    )

  def kinesisProducer: FlinkKinesisProducer[Person] =
    new FlinkKinesisProducer[Person](new ProtoSerializer[Person](), producerProperties)

  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

  env.registerTypeWithKryoSerializer(classOf[Person], classOf[ProtobufSerializer])

  env
    .addSource(kinesisConsumer)
    .addSink(kinesisProducer)

}
