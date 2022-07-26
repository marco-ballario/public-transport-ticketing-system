package it.polito.wa2.g12.catalogueservice.service.impl

import it.polito.wa2.g12.catalogueservice.dto.OrderDTO
import it.polito.wa2.g12.catalogueservice.dto.PaymentInfoDTO
import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import it.polito.wa2.g12.catalogueservice.dto.UserProfileDTO
import it.polito.wa2.g12.catalogueservice.entity.Order
import it.polito.wa2.g12.catalogueservice.entity.Ticket
import it.polito.wa2.g12.catalogueservice.entity.toDTO
import it.polito.wa2.g12.catalogueservice.enum.TicketType
import it.polito.wa2.g12.catalogueservice.kafka.BillingMessage
import it.polito.wa2.g12.catalogueservice.repository.OrderRepository
import it.polito.wa2.g12.catalogueservice.repository.TicketRepository
import it.polito.wa2.g12.catalogueservice.service.CatalogueService
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
class CatalogueServiceImpl : CatalogueService {
    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Value("\${kafka.topics.payment}")
    lateinit var topic: String

    override fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    override fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAll().map { it.toDTO() }
    }

    override fun getAllUserOrders(username: String): Flow<OrderDTO> {
        return orderRepository.findAllByUsername(username).map { it.toDTO() }
    }

    override suspend fun getUserOrder(username: String, orderId: Long): OrderDTO? {
        return orderRepository.findByUsernameAndId(username, orderId)?.toDTO()
    }

    override suspend fun addNewTicket(t: TicketDTO): TicketDTO? {
        // Only ordinary tickets have the duration set in hours
        // The other types of tickets have the duration set to null
        // The duration is the validity time of the ticket after being validated
        if ((t.type == TicketType.Ordinary && t.duration == null) ||
            (t.type != TicketType.Ordinary && t.duration != null))
            return null

        return ticketRepository.save(
            Ticket(t.name, t.type.name, t.duration, t.zones, t.price, t.min_age, t.max_age)
        ).toDTO()
    }

    private fun isValidAge(ticket: TicketDTO, profile: UserProfileDTO): Boolean {
        val calendar = Calendar.getInstance()
        val localTime = LocalDate.now()

        calendar.time = profile.date_of_birth
        val age = (calendar.get(Calendar.YEAR) - localTime.year).absoluteValue

        if ((ticket.max_age != null && age > ticket.max_age) ||
            (ticket.min_age != null && age < ticket.min_age))
            return false
        return true
    }

    override suspend fun shopTickets(username: String, paymentInfo: PaymentInfoDTO, jwt: String): OrderDTO {
        // Gets the ticket from the catalogue
        val ticket: TicketDTO = ticketRepository.findById(paymentInfo.ticket_id)?.toDTO() ?:
            // Ticket not present in the catalogue
             return orderRepository.save(
                Order(paymentInfo.ticket_id, paymentInfo.quantity, username, "FAILED")
            ).toDTO()

        // Gets profile information about the purchaser
        val response: UserProfileDTO = WebClient
            .create("http://localhost:8082")
            .get()
            .uri("/my/profile")
            .header("Authorization", jwt)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        // Age requirements not respected
        if (!isValidAge(ticket, response)) {
            return orderRepository.save(
                Order(paymentInfo.ticket_id, paymentInfo.quantity, username, "FAILED")
            ).toDTO()
        }

        var newOrder = Order(paymentInfo.ticket_id, paymentInfo.quantity, username, "PENDING")
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
    }

    override suspend fun updateTicket(ticketId: Long, updatedTicket: TicketDTO): TicketDTO? {
        if (updatedTicket.id != null && updatedTicket.id != ticketId)
            return null
        if ((updatedTicket.type == TicketType.Ordinary && updatedTicket.duration == null) ||
            (updatedTicket.type != TicketType.Ordinary && updatedTicket.duration != null))
            return null
        val newTicket: Ticket = ticketRepository.findById(ticketId) ?: return null
        newTicket.name = updatedTicket.name
        newTicket.type = updatedTicket.type.name
        newTicket.duration = updatedTicket.duration
        newTicket.zones = updatedTicket.zones
        newTicket.price = updatedTicket.price
        newTicket.min_age = updatedTicket.min_age
        newTicket.max_age = updatedTicket.max_age
        ticketRepository.save(newTicket)
        return newTicket.toDTO()
    }
}
