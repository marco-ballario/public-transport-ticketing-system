package it.polito.wa2.g12.machinemock.dto


import java.time.LocalDateTime

data class TransitDTO(
    val id : Long,
    var transit_date: LocalDateTime,
    var ticket_id: Long,
)
