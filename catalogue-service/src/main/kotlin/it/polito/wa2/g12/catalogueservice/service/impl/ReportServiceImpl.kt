package it.polito.wa2.g12.catalogueservice.service.impl

import it.polito.wa2.g12.catalogueservice.dto.PercentagesDTO
import it.polito.wa2.g12.catalogueservice.entity.toDTO
import it.polito.wa2.g12.catalogueservice.enum.TicketType
import it.polito.wa2.g12.catalogueservice.repository.OrderRepository
import it.polito.wa2.g12.catalogueservice.repository.TicketRepository
import it.polito.wa2.g12.catalogueservice.service.ReportService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono

@Service
class ReportServiceImpl: ReportService {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    override suspend fun getOrderInfo(ordersID: List<Int>): PercentagesDTO {
        val tickets = ordersID.map { orderRepository.findById(it.toLong())!! }.map { ticketRepository.findById(it.ticketId)!!.toDTO() }
        return PercentagesDTO(
            (100*tickets.count { it.type == TicketType.values().find { type -> type.name == "Ordinary" } }.toLong() / tickets.count().toLong()),
            (100*tickets.count { it.type == TicketType.values().find { type -> type.name != "Ordinary" } }.toLong() / tickets.count().toLong()),
            tickets.count())
    }
}