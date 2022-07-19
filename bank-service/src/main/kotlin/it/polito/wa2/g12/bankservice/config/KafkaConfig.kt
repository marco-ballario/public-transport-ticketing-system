package it.polito.wa2.g12.bankservice.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${kafka.bootstrapAddress}")
    private val servers: String,
    @Value("\${kafka.topics.bank}")
    private val topic: String,
    @Value("\${kafka.topics.bankPayment}")
    private val topicBankPayment: String
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    @Bean
    fun payment(): NewTopic {
        return NewTopic(topic, 1, 1.toShort())
    }

    @Bean
    fun transaction(): NewTopic {
        return NewTopic(topicBankPayment, 1, 1.toShort())
    }
}