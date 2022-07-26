package it.polito.wa2.g12.transitservice.controller

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.service.impl.TransitServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

data class Body(val ticket_id : Long)

@RestController
class TransitController(val transitService: TransitServiceImpl){

    @GetMapping("/admin/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    fun getAllTransit() : Flow<TransitDTO> {
        return  transitService.getAllTransits()
    }

    @PostMapping("/transits")
    @PreAuthorize("hasAuthority('MACHINE')")
    fun insertNewTransit(@RequestBody body : Body) : Mono<TransitDTO> {
        val ticketId = body.ticket_id
        return  transitService.insertNewTransit(ticketId)
    }

    @PostMapping("/admin/report/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun getReportTransit(
        @RequestBody dataRange: TimePeriodDTO
    ) : Int {
        return  transitService.getRepTransits(dataRange)
    }

}