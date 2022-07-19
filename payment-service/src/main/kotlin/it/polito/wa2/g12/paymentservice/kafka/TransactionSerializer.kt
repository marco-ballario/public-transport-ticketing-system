package it.polito.wa2.g12.paymentservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g12.catalogueservice.kafka.TransactionMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class TransactionSerializer : Serializer<TransactionMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: TransactionMessage?): ByteArray? {
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing Product to ByteArray[]")
        )
    }

    override fun close() {}
}
