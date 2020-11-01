package example.serdes

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation

class ProtoDeserializer[T <: com.google.protobuf.Message](val defaultInstance: T)
    extends DeserializationSchema[T] {

  override def deserialize(message: Array[Byte]): T =
    defaultInstance.getParserForType.parseFrom(message).asInstanceOf[T]

  override def isEndOfStream(nextElement: T) = false

  override def getProducedType: TypeInformation[T] =
    TypeInformation.of(defaultInstance.getClass.asInstanceOf[Class[T]])

}
