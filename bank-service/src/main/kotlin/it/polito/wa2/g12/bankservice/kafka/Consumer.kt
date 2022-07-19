package it.polito.wa2.g12.bankservice.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class Consumer {
    @Value("\${kafka.topics.bankPayment}")
    lateinit var topic: String

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @KafkaListener(topics = ["\${kafka.topics.bank}"], groupId = "ppr")
    fun paymentListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val bankMessage: BankMessage = consumerRecord.value() as BankMessage
        println(bankMessage)
        //check credit card ifo are correct
        val format = SimpleDateFormat("MM/yy")
        val exp = format.parse(bankMessage.exp)
        if ((bankMessage.ccn.length == 16 || bankMessage.ccn.length == 15) && bankMessage.card_holder.isNotEmpty() && bankMessage.cvv.length == 3 && exp.after(
                Date()
            ) && kotlin.random.Random.nextInt(0, 100) < 70
        ) {
            val message: Message<BankPaymentMessage> = MessageBuilder
                .withPayload(BankPaymentMessage(bankMessage.transaction_id, "SUCCESS", bankMessage.jwt))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            println(message)
        } else {
            val message: Message<BankPaymentMessage> = MessageBuilder
                .withPayload(BankPaymentMessage(bankMessage.transaction_id, "FAILURE", bankMessage.jwt))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            println(message)
        }
        ack.acknowledge()
    }
}