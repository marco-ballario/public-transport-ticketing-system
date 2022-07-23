package it.polito.wa2.g12.catalogueservice.entity

import it.polito.wa2.g12.catalogueservice.dto.OrderDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Column var ticketId: Long,
    @Column var quantity: Int,
    @Column var username: String,
    @Column var status: String
) {
    @Id var id: Long? = null
}

fun Order.toDTO() = OrderDTO(id!!, ticketId, quantity, username, status)