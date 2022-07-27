package it.polito.wa2.g12.catalogueservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class SuccessfulOrderSerializer : Serializer<SuccessfulOrderMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: SuccessfulOrderMessage?): ByteArray? {
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing SuccessfulOrderMessage to ByteArray[]")
        )
    }

    override fun close() {}
}
