package example.config

import java.util
import java.util.Properties

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime
import com.amazonaws.services.kinesisanalytics.runtime.models.PropertyGroup
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper

import scala.collection.JavaConversions._
import scala.util.Try

/**
  * Loads Environment properties from Kinesis Analytics Runtime, with `Environment.local.json` as a fallback.
  */
object EnvironmentLoader extends LazyLogging {

  lazy val properties: util.Map[String, Properties] = load()
  lazy val consumerProperties: Properties = properties.get("ConsumerConfigProperties")
  lazy val producerProperties: Properties = properties.get("ProducerConfigProperties")

  lazy val flinkOutputStream: String = producerProperties.getProperty("flink.stream.output")
  lazy val flinkInputStream: String = consumerProperties.getProperty("flink.stream.input")

  lazy val exampleCustomProps: Properties = properties.get("ExampleCustomConfigProperties")

  lazy val exampleProperty: String = exampleCustomProps.getProperty("example.custom.key")

  private def load(): util.Map[String, Properties] = {
    val props = Try {
      KinesisAnalyticsRuntime.getApplicationProperties()
    } match {
      case scala.util.Success(map) if !map.isEmpty => map
      case _ => parseEnvironmentProperties()
    }
    logger.info(s"Loaded application properties: \n${props.entrySet().toArray.mkString("\n")}")
    props
  }

  private def parseEnvironmentProperties(): util.Map[String, Properties] = {
    val input = getClass.getResourceAsStream("/Environment.local.json")
    val appProperties = new util.HashMap[String, Properties]
    val mapper = new ObjectMapper()
    mapper
      .readTree(input)
      .foreach { elem =>
        val propertyGroup = mapper.treeToValue(elem, classOf[PropertyGroup])
        val properties = new Properties()
        properties.putAll(propertyGroup.properties)
        appProperties.put(propertyGroup.groupID, properties)
      }
    appProperties
  }

}
