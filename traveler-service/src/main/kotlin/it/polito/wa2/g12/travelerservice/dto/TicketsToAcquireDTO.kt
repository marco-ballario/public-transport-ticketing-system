package it.polito.wa2.g12.travelerservice.dto

data class TicketsToAcquireDTO(
    val type: String,
    val quantity: Int,
    val zones: String,
    val duration: Int,
    val only_weekends: Boolean,
    val username: String,
)