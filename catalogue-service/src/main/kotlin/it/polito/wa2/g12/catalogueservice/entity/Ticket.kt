package it.polito.wa2.g12.catalogueservice.entity

import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import it.polito.wa2.g12.catalogueservice.enum.TicketType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tickets")
data class Ticket(
    @Column var name: String,
    @Column var type: String,
    @Column var duration: Int?,
    @Column var zones: String,
    @Column var price: Double,
    @Column var min_age: Int?,
    @Column var max_age: Int?,
) {
    @Id
    var id: Long? = null
}

fun Ticket.toDTO() =
    TicketDTO(id!!, name, TicketType.values().find { it.name == type }!!, duration, zones, price, min_age, max_age)