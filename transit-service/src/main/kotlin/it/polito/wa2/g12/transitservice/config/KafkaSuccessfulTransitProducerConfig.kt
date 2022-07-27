package it.polito.wa2.g12.transitservice.config

import it.polito.wa2.g12.transitservice.message.serializer.SuccessfulTransitSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaSuccessfulTransitProducerConfig(
    @Value("\${kafka.bootstrapAddress}") private val servers: String
) {

    @Bean
    fun successfulTransitProducerFactory(): ProducerFactory<String, Any> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = SuccessfulTransitSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun successfulTransitKafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(successfulTransitProducerFactory())
    }
}