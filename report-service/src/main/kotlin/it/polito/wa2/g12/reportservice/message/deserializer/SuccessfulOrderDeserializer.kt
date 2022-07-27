package it.polito.wa2.g12.reportservice.message.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g12.reportservice.message.SuccessfulOrderMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class SuccessfulOrderDeserializer : Deserializer<SuccessfulOrderMessage> {
    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): SuccessfulOrderMessage? {
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to SuccessfulOrderMessage"), Charsets.UTF_8
            ), SuccessfulOrderMessage::class.java
        )
    }

    override fun close() {}
}