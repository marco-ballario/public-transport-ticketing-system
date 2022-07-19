package it.polito.wa2.g12.catalogueservice.service.impl

import it.polito.wa2.g12.catalogueservice.dto.OrderDTO
import it.polito.wa2.g12.catalogueservice.dto.PaymentInfoDTO
import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import it.polito.wa2.g12.catalogueservice.dto.UserProfileDTO
import it.polito.wa2.g12.catalogueservice.entity.Order
import it.polito.wa2.g12.catalogueservice.entity.Ticket
import it.polito.wa2.g12.catalogueservice.entity.toDTO
import it.polito.wa2.g12.catalogueservice.kafka.BillingMessage
import it.polito.wa2.g12.catalogueservice.repository.OrderRepository
import it.polito.wa2.g12.catalogueservice.repository.TicketRepository
import it.polito.wa2.g12.catalogueservice.service.catalogueservice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.math.absoluteValue


@Service
class catalogueserviceImpl : catalogueservice {
    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Value("\${kafka.topics.payment}")
    lateinit var topic: String

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>


    override fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAllTickets().map { it.toDTO() }
    }

    override fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAllOrders().map { it.toDTO() }
    }

    override fun getAllUserOrders(username: String): Flow<OrderDTO> {
        return orderRepository.findAllUserOrders(username).map { it.toDTO() }
    }

    override suspend fun getUserOrder(username: String, orderId: Long): OrderDTO? {
        return orderRepository.findUserOrderById(username, orderId)?.toDTO()
    }

    override suspend fun addNewTicket(t: TicketDTO): TicketDTO? {
        return ticketRepository.save(
            Ticket(
                t.ticket_type,
                t.price,
                t.zones,
                t.minimum_age,
                t.maximum_age,
                t.duration,
                t.only_weekends
            )
        ).toDTO()
    }

    private fun isValidAge(ticket: TicketDTO, profile: UserProfileDTO): Boolean {
        val calendar = Calendar.getInstance()
        val localTime = LocalDate.now()

        calendar.time = profile.date_of_birth
        val age = (calendar.get(Calendar.YEAR) - localTime.year).absoluteValue
        return age <= ticket.maximum_age && age >= ticket.minimum_age
    }

    override suspend fun shopTickets(username: String, paymentInfo: PaymentInfoDTO, jwt: String): OrderDTO {
        val ticket: TicketDTO? = ticketRepository.findById(paymentInfo.ticket_id)?.toDTO()
        val response: UserProfileDTO = WebClient
            .create("http://localhost:8081")
            .get()
            .uri("/my/profile")
            .header("Authorization", jwt)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()

        // Ticket not found
        if (ticket == null) {
            val order = orderRepository.save(Order(paymentInfo.quantity, "FAILURE", username, paymentInfo.ticket_id))
            return order.toDTO()
        }

        if (isValidAge(ticket, response)) {
            var newOrder = Order(paymentInfo.quantity, "PENDING", username, paymentInfo.ticket_id)
            newOrder = orderRepository.save(newOrder)
            val price = BigDecimal.valueOf(paymentInfo.quantity * ticket.price)
            val message: Message<BillingMessage> = MessageBuilder
                .withPayload(
                    BillingMessage(
                        newOrder.id!!.toInt(),
                        price,
                        paymentInfo.card_number,
                        paymentInfo.card_expiration,
                        paymentInfo.card_cvv.toString(),
                        paymentInfo.card_holder,
                        username,
                        jwt
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)

            return newOrder.toDTO()
        } else {
            val order = orderRepository.save(Order(paymentInfo.quantity, "FAILURE", username, paymentInfo.ticket_id))
            return order.toDTO()
        }
    }
}
