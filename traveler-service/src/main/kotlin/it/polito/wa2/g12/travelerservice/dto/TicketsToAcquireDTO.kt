package it.polito.wa2.g12.travelerservice.dto

data class TicketsToAcquireDTO(
    val type: String,
    val duration: Int,
    val zones: String,
    val quantity: Int,
    val username: String,
    val only_weekends: Boolean
)