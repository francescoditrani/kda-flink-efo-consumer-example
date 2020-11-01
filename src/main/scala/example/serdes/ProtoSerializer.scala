package example.serdes

import org.apache.flink.api.common.serialization.SerializationSchema

class ProtoSerializer[T <: com.google.protobuf.Message]() extends SerializationSchema[T] {

  override def serialize(element: T): Array[Byte] = element.toByteArray

}
