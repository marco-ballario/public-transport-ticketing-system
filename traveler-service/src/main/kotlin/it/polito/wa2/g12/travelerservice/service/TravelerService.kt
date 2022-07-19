package it.polito.wa2.g12.travelerservice.service

import it.polito.wa2.g12.travelerservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketsToAcquireDTO
import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO

interface TravelerService {
    fun getUserDet(name: String): UserInfoDTO?
    fun getUserDetById(userId: Long): UserInfoDTO?
    fun updateUserDet(name: String, info: UserInfoDTO): Int
    fun getUserTickets(name: String): List<AcquiredTicketDTO>?
    fun getTicketsByUserId(userId: Long): List<AcquiredTicketDTO>?
    fun createUserTickets(name: String, quantity: Int, zone: String): List<TicketDTO>?
    fun getTravelers(): List<String>
    fun acquireTickets(ticketsToAcquire: TicketsToAcquireDTO): List<AcquiredTicketDTO>?
}