package it.polito.wa2.g12.transitservice.controller

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TicketDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.dto.TransitsStatsDTO
import it.polito.wa2.g12.transitservice.service.impl.TransitServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono



@RestController
class TransitController(val transitService: TransitServiceImpl){

    @GetMapping("/admin/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    fun getAllTransit() : Flow<TransitDTO> {
        return  transitService.getAllTransits()
    }

    @PostMapping("/transits")
    @PreAuthorize("hasAuthority('MACHINE')")
    suspend fun insertNewTransit(@RequestBody body : TicketDTO) : TransitDTO {
        return transitService.insertNewTransit(body)
    }

    @PostMapping("/admin/report/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun getReportTransit(
        @RequestBody dataRange: TimePeriodDTO
    ) : TransitsStatsDTO {
        return  transitService.getRepTransits(dataRange)
    }

    @PostMapping("/admin/report/{username}/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun getUserReportTransit(
        @RequestBody dataRange: TimePeriodDTO,
        @PathVariable username: String,
    ) : TransitsStatsDTO {
        return  transitService.getUserRepTransits(dataRange, username)
    }

}