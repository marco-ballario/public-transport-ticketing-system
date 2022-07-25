package it.polito.wa2.g12.paymentservice.dto

data class ReportDTO (
    val ticketPurchased : Int,
    val profit : Float,
    val transits : Int
)