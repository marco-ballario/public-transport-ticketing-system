package it.polito.wa2.g12.travelerservice.service.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import io.jsonwebtoken.Jwts
import it.polito.wa2.g12.travelerservice.dto.AcquiredTicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketDTO
import it.polito.wa2.g12.travelerservice.dto.TicketsToAcquireDTO
import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO
import it.polito.wa2.g12.travelerservice.entities.TicketPurchased
import it.polito.wa2.g12.travelerservice.entities.UserDetails
import it.polito.wa2.g12.travelerservice.entities.toDTO
import it.polito.wa2.g12.travelerservice.entities.toExtendedDTO
import it.polito.wa2.g12.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.g12.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.g12.travelerservice.service.TravelerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
        val ticketList: MutableList<AcquiredTicketDTO> = mutableListOf()
        tickets.forEach { t ->
            val parts = t.split(",")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val exp = formatter.parse(parts[2]).time
            val iat = formatter.parse(parts[1]).time
            val validfrom = formatter.parse(parts[5]).time

            val claims = mapOf<String, Any>(
                "sub" to parts[0].toLong(),
                "iat" to iat,
                "validfrom" to validfrom,
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
                val exp = newTicket.deadline.time
                val iat = newTicket.issuedAt.time
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

        val acquiredTickets = mutableListOf<AcquiredTicketDTO>()

        for (i in 1..ticketsToAcquire.quantity) {

            // Creates the ticket
            val t = TicketPurchased(ticketsToAcquire.zones, user.get())
            t.type = ticketsToAcquire.type
            t.issuedAt = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

            if (ticketsToAcquire.only_weekends) {
                val saturday = LocalDateTime.now().with(DayOfWeek.SATURDAY).truncatedTo(ChronoUnit.SECONDS)
                t.validFrom = java.sql.Timestamp.valueOf(saturday)
                t.deadline = java.sql.Timestamp.valueOf(saturday.plusHours(ticketsToAcquire.duration.toLong()))
            } else {
                val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                t.validFrom = java.sql.Timestamp.valueOf(now)
                t.deadline = java.sql.Timestamp.valueOf(now.plusHours(ticketsToAcquire.duration.toLong()))
            }

            // Saves the tickets
            val newTicket = ticketsRepo.save(t)

            // Generates JWS
            val claims = mapOf<String, Any>(
                "sub" to newTicket.getId()!!,
                "iat" to newTicket.issuedAt.time,
                "validfrom" to newTicket.validFrom!!.time,
                "exp" to newTicket.deadline.time,
                "zid" to newTicket.zone,
                "type" to newTicket.type!!,
            )
            val jws = Jwts.builder().setClaims(claims).signWith(secretKey).compact()
            acquiredTickets.add(newTicket.toExtendedDTO(newTicket.getId(), jws))
        }

        return acquiredTickets
    }

    override fun getQRCode(ticketId: Long, username:String) : String {
        val ticket = ticketsRepo.findById(ticketId).get()
        //generate jws
        val exp = ticket.deadline.time
        val iat = ticket.issuedAt.time
        val claims =
            mapOf<String, Any>("sub" to ticket.getId()!!, "exp" to exp, "vz" to ticket.zone, "iat" to iat,"user" to username, "type" to ticket.type)
        val jws = Jwts.builder().setClaims(claims).signWith(secretKey).compact()
        //val qr = Encoder.encode(jws,ErrorCorrectionLevel.M)
        val qr = MultiFormatWriter().encode(jws, BarcodeFormat.QR_CODE, 50, 50)
        val bos = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(qr, "png", bos)
        val image = Base64.getEncoder().encodeToString(bos.toByteArray())
        return image
    }
    // Resets the time part of a Date object to zero
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