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
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class Consumer {
    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @KafkaListener(topics = ["\${kafka.topics.transaction}"], groupId = "ppr")
    fun paymentListener(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val message = consumerRecord.value() as TransactionMessage
        if (message.status == "FAILURE") {
            mono {
                var order = orderRepository.findById(message.order_id.toLong())
                order
            }.subscribe {
                it.status = message.status
                mono { orderRepository.save(it) }.subscribe { ack.acknowledge() }
            }
        } else {
            mono {
                var order = runBlocking { orderRepository.findById(message.order_id.toLong()) }
                val ticket: TicketDTO? = runBlocking { ticketRepository.findById(order!!.ticketId)?.toDTO() }
                val ticketsToAcquire = TicketsToAcquireDTO(
                    ticket!!.ticket_type,
                    order!!.quantity,
                    ticket.zones,
                    ticket.duration,
                    ticket.only_weekends,
                    order.username
                )
                val mapper = jacksonObjectMapper()
                val body = mapper.writeValueAsString(ticketsToAcquire)

                // Sends the request to generate the tickets
                val acquiredTickets: List<AcquiredTicketDTO> = WebClient
                    .create("http://localhost:8081")
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
                acquiredTickets
            }.subscribe {
                // print tickets acquired for debug purposes
                println(it)
                ack.acknowledge()
            }
        }
    }
}