package it.polito.wa2.g12.transitservice.service.impl

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.entity.Transit
import it.polito.wa2.g12.transitservice.entity.toDTO
import it.polito.wa2.g12.transitservice.repository.TransitRepository
import it.polito.wa2.g12.transitservice.service.TransitService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class  TransitServiceImpl : TransitService {
    @Autowired
    lateinit var transitRepository: TransitRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun getAllTransits(): Flow<TransitDTO> {
        return transitRepository.findAllTransit().map { it.toDTO() }
    }

    override suspend fun getRepTransits(datarange: TimePeriodDTO): Int {
        return transitRepository.findAllTransit().map { it.transit_date }.filter {
                it.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
                it.isBefore(LocalDateTime.parse(datarange.end_date, formatter))
            }.count()
    }

    override fun insertNewTransit(ticket_id: Long): Mono<TransitDTO> {
        val now = LocalDateTime.now()
        return  mono {transitRepository.save(Transit(now,ticket_id)).toDTO()}
    }
}