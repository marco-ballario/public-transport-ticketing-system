package it.polito.wa2.g12.reportservice.message.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g12.reportservice.message.SuccessfulTransactionMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class SuccessfulTransactionDeserializer : Deserializer<SuccessfulTransactionMessage> {
    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): SuccessfulTransactionMessage? {
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to SuccessfulTransactionMessage"), Charsets.UTF_8
            ), SuccessfulTransactionMessage::class.java
        )
    }

    override fun close() {}
}