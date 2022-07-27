package it.polito.wa2.g12.reportservice.message.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g12.reportservice.message.SuccessfulTransitMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class SuccessfulTransitDeserializer : Deserializer<SuccessfulTransitMessage> {
    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): SuccessfulTransitMessage? {
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to SuccessfulTransitMessage"), Charsets.UTF_8
            ), SuccessfulTransitMessage::class.java
        )
    }

    override fun close() {}
}