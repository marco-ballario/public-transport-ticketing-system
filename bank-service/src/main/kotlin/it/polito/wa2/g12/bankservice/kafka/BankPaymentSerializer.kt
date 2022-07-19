package it.polito.wa2.g12.bankservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class BankPaymentSerializer : Serializer<BankPaymentMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: BankPaymentMessage?): ByteArray? {
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing Product to ByteArray[]")
        )
    }

    override fun close() {}
}
