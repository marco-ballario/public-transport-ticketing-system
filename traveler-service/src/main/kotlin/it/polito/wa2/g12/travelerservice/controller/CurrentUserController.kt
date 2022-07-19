package it.polito.wa2.g12.travelerservice.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g12.travelerservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketsToAcquireDTO
import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO
import it.polito.wa2.g12.travelerservice.service.impl.TravelerServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/my")
class CurrentUserController(val travelerService: TravelerServiceImpl) {

    @GetMapping(value = ["/profile"])
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    fun getUserDet(principal: Principal): ResponseEntity<Any> {
        val res: UserInfoDTO? = travelerService.getUserDet(principal.name)
        return if (res == null) ResponseEntity("User details not available for ${principal.name}", HttpStatus.NOT_FOUND)
        else ResponseEntity(res, HttpStatus.OK)
    }

    // To test this endpoint you can provide a JSON like this one:
    // {"name":"test", "address":"test", "date_of_birth":"2022-05-18", "number":"123456789"}
    // All the JSON fields are needed
    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    fun updateUserDet(
        @RequestBody
        body: String,
        br: BindingResult,
        principal: Principal
    ): ResponseEntity<String> {
        if (br.hasErrors())
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        val ob = jacksonObjectMapper()
        val newInfo: UserInfoDTO = ob.readValue(body, UserInfoDTO::class.java)
        return when (travelerService.updateUserDet(principal.name, newInfo)) {
            2 -> ResponseEntity("Cannot update the user name field", HttpStatus.BAD_REQUEST)
            1 -> ResponseEntity("User details updated!", HttpStatus.OK)
            else -> ResponseEntity("User details created", HttpStatus.CREATED)
        }
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    fun getTickets(principal: Principal): ResponseEntity<Any> {
        val res: List<AcquiredTicketDTO>? = travelerService.getUserTickets(principal.name)
        return if (res == null) ResponseEntity("UserDetails not available for ${principal.name}", HttpStatus.NOT_FOUND)
        else ResponseEntity(res, HttpStatus.OK)
    }

    private data class AddingTicketReq(val cmd: String, val quantity: Int, val zones: String)

    // To test this endpoint you can provide a JSON like this one:
    // {"cmd": "buy_tickets", "quantity": "2", "zones": "ABC"}
    // All the JSON fields are needed
    @PostMapping("/tickets")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    fun postTickets(
        @RequestBody
        body: String,
        br: BindingResult,
        principal: Principal
    ): ResponseEntity<Any> {
        val ob = jacksonObjectMapper()
        val req = ob.readValue(body, AddingTicketReq::class.java)
        if (req.cmd != "buy_tickets" || br.hasErrors())
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        val res: List<TicketDTO>? = travelerService.createUserTickets(principal.name, req.quantity, req.zones)
        return if (res == null) ResponseEntity("UserDetails not available for ${principal.name}", HttpStatus.NOT_FOUND)
        else ResponseEntity(res, HttpStatus.OK)
    }

    // This endpoint is the one called by the catalogue service to generate tickets
    @PostMapping("/tickets/acquired")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    fun aquireTickets(
        @RequestBody acquiredTickets: TicketsToAcquireDTO,
        principal: Principal
    ): ResponseEntity<List<AcquiredTicketDTO>> {
        val tickets = travelerService.acquireTickets(acquiredTickets) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        return ResponseEntity(tickets, HttpStatus.OK)
    }
}