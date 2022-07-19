package it.polito.wa2.g12.catalogueservice.entity

import it.polito.wa2.g12.catalogueservice.dto.TicketDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ticket_catalogue")
class Ticket(
    @Column
    var ticket_type: String,
    @Column
    var price: Double,
    @Column
    var zones: String,
    @Column
    var minimum_age: Int,
    @Column
    var maximum_age: Int,
    @Column
    var duration: Int,
    @Column
    var only_weekends: Boolean
) {
    @Id
    var id: Long? = null
}

fun Ticket.toDTO() = TicketDTO(id!!, price, ticket_type, zones, minimum_age, maximum_age, duration, only_weekends)