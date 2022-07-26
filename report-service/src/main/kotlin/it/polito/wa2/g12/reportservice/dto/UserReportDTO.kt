package it.polito.wa2.g12.reportservice.dto

data class UserReportDTO (
    val purchases : Int,
    val shopping : Float,
    val transits : Int,
    val avarageSpend: Float,
    val minOrder: Float,
    val maxOrder: Float,
    val percClassicTickets: Float,
    val percTravelersCards: Float,
    val percTransitsClassicTickets: Float,
    val percTransitsTravelerCards: Float,
    val ticketsNumber: Int
)