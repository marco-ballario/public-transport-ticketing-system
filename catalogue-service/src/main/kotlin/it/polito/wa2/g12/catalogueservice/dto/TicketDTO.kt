package it.polito.wa2.g12.catalogueservice.dto

class TicketDTO(
    val id: Long,
    val price: Double,
    val ticket_type: String,
    val zones: String,
    val minimum_age: Int,
    val maximum_age: Int,
    val duration: Int,
    val only_weekends: Boolean
)