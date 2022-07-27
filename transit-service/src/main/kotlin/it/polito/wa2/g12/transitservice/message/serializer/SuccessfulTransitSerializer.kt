package it.polito.wa2.g12.transitservice.message.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g12.transitservice.message.SuccessfulTransitMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class SuccessfulTransitSerializer : Serializer<SuccessfulTransitMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: SuccessfulTransitMessage?): ByteArray? {
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing SuccessfulTransitMessage to ByteArray[]")
        )
    }

    override fun close() {}
}
