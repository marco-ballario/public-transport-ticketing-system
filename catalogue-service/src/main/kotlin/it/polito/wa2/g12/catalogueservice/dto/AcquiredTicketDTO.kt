package it.polito.wa2.g12.catalogueservice.dto

data class AcquiredTicketDTO(
    val sub: Long,
    val iat: String,
    val validfrom: String,
    val exp: String,
    val zid: String,
    val type: String,
    val jws: String
)