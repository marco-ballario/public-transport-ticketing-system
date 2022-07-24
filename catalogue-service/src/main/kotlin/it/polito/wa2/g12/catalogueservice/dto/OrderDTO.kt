package it.polito.wa2.g12.catalogueservice.dto

class OrderDTO(
    val id: Long,
    val ticket_id: Long,
    val quantity: Int,
    val username: String,
    val status: String,
)