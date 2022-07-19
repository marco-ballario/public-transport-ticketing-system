package it.polito.wa2.g12.catalogueservice.repository

import it.polito.wa2.g12.catalogueservice.entity.Ticket
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TicketRepository : CoroutineCrudRepository<Ticket, Long> {
    @Query(
        """
        SELECT *
        FROM ticket_catalogue
    """
    )
    fun findAllTickets(): Flow<Ticket>
}