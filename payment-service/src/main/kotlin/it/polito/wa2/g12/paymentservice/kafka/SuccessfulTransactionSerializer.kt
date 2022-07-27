package it.polito.wa2.g12.paymentservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class SuccessfulTransactionSerializer : Serializer<SuccessfulTransactionMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: SuccessfulTransactionMessage?): ByteArray? {
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing SuccessfulTransactionMessage to ByteArray[]")
        )
    }

    override fun close() {}
}
