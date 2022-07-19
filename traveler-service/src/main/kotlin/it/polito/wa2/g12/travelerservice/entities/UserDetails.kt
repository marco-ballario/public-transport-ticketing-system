package it.polito.wa2.g12.travelerservice.entities

import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO
import java.util.*
import javax.persistence.*

@Entity
class UserDetails(
    @Column(nullable = false)
    var username: String,
    @Column(nullable = false, unique = true)
    var name: String,
    @Column(nullable = false)
    var address: String,
    @Column(nullable = false)
    @Temporal(value = TemporalType.DATE)
    var date_of_birth: Date = java.sql.Date.valueOf(""),
    @Column(nullable = false, unique = true)
    var phoneNumber: String,
    // Ticket list
    @OneToMany(mappedBy = "userDet")
    val tickets: MutableList<TicketPurchased> = mutableListOf<TicketPurchased>()
) : EntityBase<Long>() {
    fun addTicket(t: TicketPurchased) {
        t.userDet = this
        tickets.add(t)
    }
}

fun UserDetails.toDTO() = UserInfoDTO(username, address, date_of_birth, phoneNumber)