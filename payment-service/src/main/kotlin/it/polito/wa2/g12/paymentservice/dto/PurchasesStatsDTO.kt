package it.polito.wa2.g12.paymentservice.dto

data class PurchasesStatsDTO(
    val percOrdinaryTickets: Long,
    val percTravelerCards: Long,
    val ticketsNumber: Int,
)
