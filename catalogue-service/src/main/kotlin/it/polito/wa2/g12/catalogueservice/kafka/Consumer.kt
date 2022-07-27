package it.polito.wa2.g12.catalogueservice.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g12.catalogueservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import it.polito.wa2.g12.catalogueservice.dto.TicketsToAcquireDTO
import it.polito.wa2.g12.catalogueservice.entity.toDTO
import it.polito.wa2.g12.catalogueservice.repository.OrderRepository
import it.polito.wa2.g12.catalogueservice.repository.TicketRepository
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class Consumer {

    @Value("\${kafka.topics.successfulOrder}")
    lateinit var topicSuccessfulOrder: String

    @Autowired
    @Qualifier("kafkaSuccessfulOrderTemplate")
    lateinit var kafkaSuccessfulOrderTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @KafkaListener(topics = ["\${kafka.topics.transaction}"], groupId = "ppr")
    fun paymentListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val message = consumerRecord.value() as TransactionMessage
        if (message.status == "FAILED") {
            mono {
                val order = orderRepository.findById(message.order_id.toLong())
                order
            }.subscribe {
                it.status = message.status
                mono { orderRepository.save(it) }.subscribe { ack.acknowledge() }
            }
        } else {
            mono {
                val order = runBlocking { orderRepository.findById(message.order_id.toLong()) }
                val ticket: TicketDTO? = runBlocking { ticketRepository.findById(order!!.ticketId)?.toDTO() }
                val ticketsToAcquire = TicketsToAcquireDTO(
                    ticket!!.type.name,
                    ticket.duration,
                    ticket.zones,
                    order!!.quantity,
                    order.username
                )
                val mapper = jacksonObjectMapper()
                val body = mapper.writeValueAsString(ticketsToAcquire)

                // Sends the request to generate the tickets
                val acquiredTickets: List<AcquiredTicketDTO> = WebClient
                    .create("http://localhost:8082")
                    .post()
                    .uri("/my/tickets/acquired")
                    .header("Authorization", message.jwt)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .awaitBody()
                order.status = message.status
                orderRepository.save(order)

                // If successful, the order is sent to the report service
                val successfulOrderMessage: Message<SuccessfulOrderMessage> = MessageBuilder
                    .withPayload(
                        SuccessfulOrderMessage(
                            order.id,
                            ticket.type.name,
                            order.quantity,
                            order.username
                        )
                    )
                    .setHeader(KafkaHeaders.TOPIC, topicSuccessfulOrder)
                    .setHeader("X-Custom-Header", "Custom header here")
                    .build()
                kafkaSuccessfulOrderTemplate.send(successfulOrderMessage)

                acquiredTickets
            }.subscribe {
                // Prints tickets acquired for debug purposes
                println(it)
                ack.acknowledge()
            }
        }
    }
}