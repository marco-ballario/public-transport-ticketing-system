package it.polito.wa2.g12.catalogueservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import kotlin.text.Charsets.UTF_8

class TransactionDeserializer : Deserializer<TransactionMessage> {
    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): TransactionMessage? {
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
            ), TransactionMessage::class.java
        )
    }

    override fun close() {}

}
