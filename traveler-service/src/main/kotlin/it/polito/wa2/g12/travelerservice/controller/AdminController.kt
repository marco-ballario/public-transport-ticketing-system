package it.polito.wa2.g12.travelerservice.controller

import it.polito.wa2.g12.travelerservice.service.impl.TravelerServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(val travelerService: TravelerServiceImpl) {

    @GetMapping("/travelers")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAllUsers(): ResponseEntity<Any> {
        val res = travelerService.getTravelers()
        return ResponseEntity(res, HttpStatus.OK)
    }

    @GetMapping("traveler/{userID}/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getTravelerById(
        @PathVariable userID: Long
    ): ResponseEntity<Any> {
        val res = travelerService.getUserDetById(userID)
        return if (res == null) ResponseEntity("User details not available for specified id: $userID", HttpStatus.OK)
        else ResponseEntity(res, HttpStatus.OK)
    }

    @GetMapping("traveler/{userID}/tickets")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getTravelerTicketsByUserId(
        @PathVariable userID: Long
    ): ResponseEntity<Any> {
        val res = travelerService.getTicketsByUserId(userID)
        return if (res == null) ResponseEntity(
            "User details not available for specified id: $userID",
            HttpStatus.BAD_REQUEST
        )
        else if (res!!.isEmpty()) ResponseEntity("No tickets found for the specified user", HttpStatus.NOT_FOUND)
        else ResponseEntity(res, HttpStatus.OK)
    }
}
