package it.polito.wa2.g12.travelerservice.repositories

import it.polito.wa2.g12.travelerservice.entities.TicketPurchased
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TicketPurchasedRepository : CrudRepository<TicketPurchased, Long> {
    @Query("select tp.id, tp.issuedAt, tp.deadline, tp.zone, tp.userDet.id, tp.validFrom, tp.type from TicketPurchased tp where tp.userDet.id = ?1")
    fun findAllByUserDet(userId: Long): List<String>
}