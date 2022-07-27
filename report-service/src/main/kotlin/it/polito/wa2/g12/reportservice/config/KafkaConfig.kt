package it.polito.wa2.g12.reportservice.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${kafka.bootstrapAddress}") private val servers: String,
    @Value("\${kafka.topics.successfulTransaction}") private val successfulTransactionTopic: String,
    @Value("\${kafka.topics.successfulOrder}") private val successfulOrderTopic: String
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }
    
    @Bean
    fun successfulTransaction(): NewTopic {
        return NewTopic(successfulTransactionTopic, 1, 1.toShort())
    }

    @Bean
    fun successfulOrder(): NewTopic {
        return NewTopic(successfulOrderTopic, 1, 1.toShort())
    }
}