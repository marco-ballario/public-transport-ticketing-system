package it.polito.wa2.g12.paymentservice.kafka

import it.polito.wa2.g12.catalogueservice.kafka.BillingMessage
import it.polito.wa2.g12.catalogueservice.kafka.TransactionMessage
import it.polito.wa2.g12.paymentservice.entity.Transaction
import it.polito.wa2.g12.paymentservice.repository.TransactionRepository
import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Consumer {
    @Value("\${kafka.topics.transaction}")
    lateinit var topicT: String

    @Value("\${kafka.topics.bank}")
    lateinit var topic: String

    @Autowired
    @Qualifier("kafkaTemplate")
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    @Qualifier("kafkaTemplateBank")
    lateinit var kafkaTemplateBank: KafkaTemplate<String, Any>

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @KafkaListener(
        containerFactory = "kafkaListenerContainerFactory",
        topics = ["\${kafka.topics.payment}"],
        groupId = "ppr"
    )
    fun paymentListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val billingMessage: BillingMessage = consumerRecord.value() as BillingMessage
        println(billingMessage)
        mono {
            transactionRepository.save(
                Transaction(
                    billingMessage.order_id,
                    billingMessage.username,
                    billingMessage.price,
                    LocalDateTime.now(),
                    "PENDING"
                )
            )
        }.subscribe {
            val message: Message<BankMessage> = MessageBuilder
                .withPayload(
                    BankMessage(
                        it.id!!,
                        billingMessage.price,
                        billingMessage.ccn,
                        billingMessage.exp,
                        billingMessage.cvv,
                        billingMessage.card_holder,
                        billingMessage.jwt
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplateBank.send(message)
            ack.acknowledge()
        }

    }

    @KafkaListener(
        containerFactory = "kafkaListenerContainerFactoryBank",
        topics = ["\${kafka.topics.bankPayment}"],
        groupId = "ppr"
    )
    fun bankListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val bankPaymentMessage = consumerRecord.value() as BankPaymentMessage
        println(bankPaymentMessage)
        mono {
            transactionRepository.findById(bankPaymentMessage.transaction_id)
        }.subscribe {
            it.status = bankPaymentMessage.status
            mono { transactionRepository.save(it) }.subscribe { res ->
                val message: Message<TransactionMessage> = MessageBuilder
                    .withPayload(
                        TransactionMessage(
                            res.orderId,
                            res.id,
                            res.status,
                            res.username,
                            bankPaymentMessage.jwt
                        )
                    )
                    .setHeader(KafkaHeaders.TOPIC, topicT)
                    .setHeader("X-Custom-Header", "Custom header here")
                    .build()
                kafkaTemplate.send(message)
                ack.acknowledge()
            }
        }

    }
}