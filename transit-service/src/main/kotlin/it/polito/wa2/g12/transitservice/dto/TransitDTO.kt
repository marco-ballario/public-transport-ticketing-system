package it.polito.wa2.g12.transitservice.dto


import java.time.LocalDateTime

data class TransitDTO(
    val id : Long,
    val transit_date: LocalDateTime,
    val ticket_id: Long,
    val ticket_type : String,
    val ticket_user : String,

)
