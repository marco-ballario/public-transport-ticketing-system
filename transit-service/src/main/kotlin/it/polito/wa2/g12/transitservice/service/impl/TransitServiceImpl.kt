package it.polito.wa2.g12.transitservice.service.impl

import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.entity.Transit
import it.polito.wa2.g12.transitservice.entity.toDTO
import it.polito.wa2.g12.transitservice.repository.TransitRepository
import it.polito.wa2.g12.transitservice.service.TransitService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class  TransitServiceImpl : TransitService {
    @Autowired
    lateinit var transitRepository: TransitRepository

    override fun getAllTransits(): Flow<TransitDTO> {
        return transitRepository.findAllTransit().map { it.toDTO() }
    }

    override fun insertNewTransit(ticket_id: Long): Mono<TransitDTO> {
        val now = LocalDateTime.now()
        return  mono {transitRepository.save(Transit(now,ticket_id)).toDTO()}
    }
}