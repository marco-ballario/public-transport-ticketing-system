package it.polito.wa2.g12.transitservice.service

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono

interface TransitService {
    fun insertNewTransit(ticket_id : Long) : Mono<TransitDTO>
    fun getAllTransits(): Flow<TransitDTO>
    suspend fun getRepTransits(datarange: TimePeriodDTO): Int
}