package it.polito.wa2.g12.catalogueservice.dto

data class TicketsToAcquireDTO(
    val type: String,
    val duration: Int?,
    val zones: String,
    val quantity: Int,
    val username: String,
)