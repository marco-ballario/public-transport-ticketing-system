package it.polito.wa2.g12.catalogueservice.dto

import it.polito.wa2.g12.catalogueservice.enum.TicketType

class TicketDTO(
    val id: Long?,
    val name: String,
    val type: TicketType,
    val duration: Int?,
    val zones: String,
    val price: Double,
    val min_age: Int?,
    val max_age: Int?,
)