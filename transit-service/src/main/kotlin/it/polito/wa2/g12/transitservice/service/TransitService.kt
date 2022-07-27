package it.polito.wa2.g12.transitservice.service

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TicketDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.dto.TransitsStatsDTO
import kotlinx.coroutines.flow.Flow

interface TransitService {
    suspend fun insertNewTransit(ticket : TicketDTO) : TransitDTO
    fun getAllTransits(): Flow<TransitDTO>
    suspend fun getRepTransits(datarange: TimePeriodDTO): TransitsStatsDTO
    suspend fun getUserRepTransits(datarange: TimePeriodDTO, username: String): TransitsStatsDTO
}