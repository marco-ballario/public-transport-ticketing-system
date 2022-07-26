package it.polito.wa2.g12.catalogueservice.repository

import it.polito.wa2.g12.catalogueservice.entity.Ticket
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TicketRepository : CoroutineCrudRepository<Ticket, Long>