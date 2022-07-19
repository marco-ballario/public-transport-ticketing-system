package it.polito.wa2.g12.catalogueservice.entity

import it.polito.wa2.g12.catalogueservice.dto.OrderDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
class Order(
    @Column
    var quantity: Int,
    @Column
    var status: String,
    @Column
    var username: String,
    @Column
    var ticketId: Long
) {
    @Id
    var id: Long? = null
}

fun Order.toDTO() = OrderDTO(id!!, quantity, status, username, ticketId)