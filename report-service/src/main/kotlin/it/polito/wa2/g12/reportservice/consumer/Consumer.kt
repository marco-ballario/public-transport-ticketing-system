package it.polito.wa2.g12.reportservice.consumer

import it.polito.wa2.g12.reportservice.entity.Order
import it.polito.wa2.g12.reportservice.entity.Transaction
import it.polito.wa2.g12.reportservice.message.SuccessfulOrderMessage
import it.polito.wa2.g12.reportservice.message.SuccessfulTransactionMessage
import it.polito.wa2.g12.reportservice.repository.OrderRepository
import it.polito.wa2.g12.reportservice.repository.TransactionRepository
import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Consumer {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    //var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm")

    @KafkaListener(
        containerFactory = "successfulTransactionKafkaListenerContainerFactory",
        topics = ["\${kafka.topics.successfulTransaction}"],
        groupId = "ppr"
    )
    fun successfulTransactionListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val successfulTransactionMessage: SuccessfulTransactionMessage = consumerRecord.value() as SuccessfulTransactionMessage
        println(successfulTransactionMessage)
        mono {
            transactionRepository.save(
                Transaction(
                    successfulTransactionMessage.amount,
                    successfulTransactionMessage.username,
                    LocalDateTime.parse(successfulTransactionMessage.issued_at)
                )
            )
        }.subscribe {
            ack.acknowledge()
        }
    }

    @KafkaListener(
        containerFactory = "successfulOrderKafkaListenerContainerFactory",
        topics = ["\${kafka.topics.successfulOrder}"],
        groupId = "ppr"
    )
    fun successfulOrderListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val successfulOrderMessage: SuccessfulOrderMessage = consumerRecord.value() as SuccessfulOrderMessage
        println(successfulOrderMessage)
        mono {
            orderRepository.save(
                Order(
                    successfulOrderMessage.ticket_type,
                    successfulOrderMessage.quantity,
                    successfulOrderMessage.username
                )
            )
        }.subscribe {
            ack.acknowledge()
        }
    }

}