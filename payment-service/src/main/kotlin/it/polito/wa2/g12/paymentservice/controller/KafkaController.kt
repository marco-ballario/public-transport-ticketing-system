package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.kafka.BankMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class KafkaController(
    @Value("\${kafka.topics.bank}") val topicT: String,
    @Autowired
    @Qualifier("kafkaTemplateBank")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    @PostMapping
    fun post(@RequestBody text: BankMessage): ResponseEntity<Any> {
        return try {

            val message: Message<BankMessage> = MessageBuilder
                .withPayload(text)
                .setHeader(KafkaHeaders.TOPIC, topicT)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            println(message)
            kafkaTemplate.send(message)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            println(e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }
}