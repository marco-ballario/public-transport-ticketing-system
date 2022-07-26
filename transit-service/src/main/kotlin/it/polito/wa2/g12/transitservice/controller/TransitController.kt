package it.polito.wa2.g12.transitservice.controller

import it.polito.wa2.g12.transitservice.dto.TicketDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.service.impl.TransitServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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
    fun insertNewTransit(@RequestBody body : TicketDTO) : Mono<TransitDTO> {
        return  transitService.insertNewTransit(body)
    }
}