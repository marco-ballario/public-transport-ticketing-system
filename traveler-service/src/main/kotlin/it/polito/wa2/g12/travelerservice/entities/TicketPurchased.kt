package it.polito.wa2.g12.travelerservice.entities

import it.polito.wa2.g12.travelerservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketDTO
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*

@Entity
class TicketPurchased(
    @Column(nullable = false)
    var zone: String,
    @ManyToOne
    var userDet: UserDetails
) : EntityBase<Long>() {
    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var issuedAt: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var deadline: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))

    @Column(nullable = true)
    @Temporal(value = TemporalType.TIMESTAMP)
    var validFrom: Date? = null

    @Column(nullable = true)
    var type: String? = null
}

fun TicketPurchased.toDTO(ticketId: Long?, jws: String) =
    TicketDTO(ticketId, zone, issuedAt.toString(), deadline.toString(), jws)

fun TicketPurchased.toExtendedDTO(ticketId: Long?, jws: String) =
    AcquiredTicketDTO(
        ticketId!!,
        issuedAt.toString(),
        validFrom.toString(),
        deadline.toString(),
        zone,
        type!!,
        jws
    )