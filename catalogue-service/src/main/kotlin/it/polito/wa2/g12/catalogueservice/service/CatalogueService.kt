package it.polito.wa2.g12.catalogueservice.service

import it.polito.wa2.g12.catalogueservice.dto.OrderDTO
import it.polito.wa2.g12.catalogueservice.dto.PaymentInfoDTO
import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import kotlinx.coroutines.flow.Flow

interface catalogueservice {
    fun getAllTickets(): Flow<TicketDTO>
    fun getAllOrders(): Flow<OrderDTO>
    fun getAllUserOrders(username: String): Flow<OrderDTO>
    suspend fun getUserOrder(username: String, orderId: Long): OrderDTO?
    suspend fun addNewTicket(t: TicketDTO): TicketDTO?
    suspend fun shopTickets(username: String, paymentInfo: PaymentInfoDTO, jwt: String): OrderDTO
}