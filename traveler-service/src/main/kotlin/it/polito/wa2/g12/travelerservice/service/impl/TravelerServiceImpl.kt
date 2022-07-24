package it.polito.wa2.g12.travelerservice.service.impl

import io.jsonwebtoken.Jwts
import it.polito.wa2.g12.travelerservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketsToAcquireDTO
import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO
import it.polito.wa2.g12.travelerservice.entities.TicketPurchased
import it.polito.wa2.g12.travelerservice.entities.UserDetails
import it.polito.wa2.g12.travelerservice.entities.toDTO
import it.polito.wa2.g12.travelerservice.entities.toExtendedDTO
import it.polito.wa2.g12.travelerservice.enum.TicketType
import it.polito.wa2.g12.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.g12.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.g12.travelerservice.service.TravelerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.*
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey


@Service
class TravelerServiceImpl : TravelerService {

    @Autowired
    lateinit var userDetRepo: UserDetailsRepository

    @Autowired
    lateinit var ticketsRepo: TicketPurchasedRepository

    @Autowired
    lateinit var secretKey: SecretKey

    override fun getUserDet(name: String): UserInfoDTO? {
        return if (userDetRepo.findByName(name).isEmpty) null
        else userDetRepo.findByName(name).get().toDTO()
    }

    override fun getUserDetById(userId: Long): UserInfoDTO? {
        val user = userDetRepo.findById(userId)
        return if (user.isEmpty) null
        else user.get().toDTO()
    }

    //returns "0" if the record is created
    //returns "1" if the record is updated
    //returns "2" otherwise
    override fun updateUserDet(name: String, info: UserInfoDTO): Int {
        val userInfo: Optional<UserDetails> = userDetRepo.findByName(name)
        val userDet: Optional<UserDetails> = userDetRepo.findByName(name)
        return if (userInfo.isEmpty) {
            userDetRepo.save(UserDetails(info.name, name, info.address, info.date_of_birth, info.number))
            0
        } else if (!userDet.isEmpty && userInfo.get().getId() == userDet.get().getId()) {
            userInfo.get().username = info.name
            userInfo.get().address = info.address
            userInfo.get().date_of_birth = info.date_of_birth
            userInfo.get().phoneNumber = info.number
            userDetRepo.save(userInfo.get())
            1
        } else 2
    }

    private fun getTicketList(tickets: List<String>): MutableList<AcquiredTicketDTO> {
        // 0=id, 1=issuedAt, 2=deadline, 3=zone, 4=userDet.id, 5=validFrom, 6=type
        val ticketList: MutableList<AcquiredTicketDTO> = mutableListOf()
        tickets.forEach { t ->
            val parts = t.split(",")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val exp: Date = formatter.parse(parts[2])
            val iat: Date = formatter.parse(parts[1])
            val validfrom: Date = formatter.parse(parts[5])

            val claims = mapOf<String, Any>(
                "sub" to parts[0].toLong(),
                "iat" to iat,
                "nbf" to validfrom,
                "exp" to exp,
                "zid" to parts[3],
                "type" to parts[6],
            )
            val jws = Jwts.builder().setClaims(claims).signWith(secretKey).compact()
            ticketList.add(AcquiredTicketDTO(parts[0].toLong(), parts[1], parts[5], parts[2], parts[3], parts[6], jws))
        }
        return ticketList
    }

    override fun getUserTickets(name: String): List<AcquiredTicketDTO>? {
        val userInfo: Optional<UserDetails> = userDetRepo.findByName(name)
        return if (userInfo.isEmpty) null
        else {
            val tickets: List<String> = ticketsRepo.findAllByUserDet(userInfo.get().getId()!!)
            getTicketList(tickets)
        }
    }

    override fun getTicketsByUserId(userId: Long): List<AcquiredTicketDTO>? {
        return if (userDetRepo.findById(userId).isEmpty) null
        else {
            val tickets: List<String> = ticketsRepo.findAllByUserDet(userId)
            getTicketList(tickets)
        }
    }

    override fun createUserTickets(name: String, quantity: Int, zone: String): List<TicketDTO>? {
        val userInfo: Optional<UserDetails> = userDetRepo.findByName(name)
        return if (userInfo.isEmpty) null
        else {
            val user: UserDetails = userInfo.get()
            var x = quantity
            val newTickets: MutableList<TicketDTO> = mutableListOf()
            while (x > 0) {
                var newTicket = TicketPurchased(zone, user)
                newTicket = ticketsRepo.save(newTicket)
                val exp = newTicket.deadline
                val iat = newTicket.issuedAt
                val claims =
                    mapOf<String, Any>("sub" to newTicket.getId()!!, "exp" to exp, "vz" to newTicket.zone, "iat" to iat)
                val jws = Jwts.builder().setClaims(claims).signWith(secretKey).compact()
                newTickets.add(newTicket.toDTO(newTicket.getId(), jws))
                x--
            }
            newTickets
        }
    }

    override fun getTravelers(): List<String> {
        return userDetRepo.findAllTravelers()
    }

    // Generates the tickets requested from the catalogue service
    override fun acquireTickets(ticketsToAcquire: TicketsToAcquireDTO): List<AcquiredTicketDTO>? {
        // Gets user from the db
        val user = userDetRepo.findByName(ticketsToAcquire.username)
        if (user.isEmpty)
            return null

        var calendar = Calendar.getInstance()
        val acquiredTickets = mutableListOf<AcquiredTicketDTO>()

        for (i in 1..ticketsToAcquire.quantity) {

            // Creates the ticket
            val t = TicketPurchased(ticketsToAcquire.zones, user.get())
            t.type = ticketsToAcquire.type
            t.issuedAt = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

            when (TicketType.values().find { it.name == ticketsToAcquire.type }!!) {
                TicketType.Daily -> {
                    val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = java.sql.Timestamp.valueOf(now)
                    t.deadline = java.sql.Timestamp.valueOf(now.plusHours(ticketsToAcquire.duration.toLong()))
                }
                TicketType.Weekend -> {
                    val saturday = LocalDateTime.now().with(DayOfWeek.SATURDAY).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(saturday))
                    t.deadline = resetTime(calendar, java.sql.Timestamp.valueOf(saturday.plusHours(24 * 2)))
                }
                TicketType.Weekly -> {
                    val monday = LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(monday))
                    t.deadline = resetTime(calendar, java.sql.Timestamp.valueOf(monday.plusHours(24 * 7)))
                }
                TicketType.Monthly -> {
                    val firstDay = LocalDateTime.now().with(firstDayOfMonth()).truncatedTo(ChronoUnit.SECONDS)
                    val lastDay = LocalDateTime.now().with(lastDayOfMonth()).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(firstDay))
                    t.deadline = resetTime(calendar, java.sql.Timestamp.valueOf(lastDay.plusHours(24)))
                }
                TicketType.Yearly -> {
                    val firstDay = LocalDateTime.now().with(firstDayOfYear()).truncatedTo(ChronoUnit.SECONDS)
                    val lastDay = LocalDateTime.now().with(lastDayOfYear()).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(firstDay))
                    t.deadline = resetTime(calendar, java.sql.Timestamp.valueOf(lastDay.plusHours(24)))
                }
            }
            println("AIUTO")
            println(t.validFrom)
            // Saves the tickets
            val newTicket = ticketsRepo.save(t)

            // Generates JWS
            val claims = mapOf<String, Any>(
                "sub" to newTicket.getId()!!,
                "iat" to newTicket.issuedAt,
                "nbf" to newTicket.validFrom,
                "exp" to newTicket.deadline,
                "zid" to newTicket.zone,
                "type" to newTicket.type!!,
            )
            val jws = Jwts.builder().setClaims(claims).signWith(secretKey).compact()
            acquiredTickets.add(newTicket.toExtendedDTO(newTicket.getId(), jws))
        }

        return acquiredTickets
    }

    private fun resetTime(calendar: Calendar, date: Date): Date {
        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    // Creates a default record in the db
    @PostConstruct
    private fun createAdmin() {
        if (userDetRepo.findByName("admin").isEmpty) {
            userDetRepo.save(
                UserDetails(
                    "admin",
                    "admin",
                    "address",
                    Date(0),
                    "0123456789",
                )
            )
        }
    }
}