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
import it.polito.wa2.g12.travelerservice.enum.TicketType
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
        val calendar = Calendar.getInstance()
        tickets.forEach { t ->
            val parts = t.split(",")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
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
            ticketList.add(
                AcquiredTicketDTO(
                    parts[0].toLong(),
                    parts[1].substring(0, parts[1].length -2),
                    parts[5].substring(0, parts[5].length -2),
                    parts[2].substring(0, parts[2].length -2),
                    parts[3],
                    parts[6],
                    jws)
            )
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

                val newTicketDTO = newTicket.toDTO(newTicket.getId(), jws)
                newTicketDTO.iat = newTicketDTO.iat.substring(0, newTicketDTO.iat.length -2)
                newTicketDTO.exp = newTicketDTO.exp.substring(0, newTicketDTO.exp.length -2)
                newTickets.add(newTicketDTO)
                newTicket
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

        val calendar = Calendar.getInstance()
        val acquiredTickets = mutableListOf<AcquiredTicketDTO>()

        for (i in 1..ticketsToAcquire.quantity) {

            // Creates the ticket
            val t = TicketPurchased(ticketsToAcquire.zones, user.get())
            t.type = ticketsToAcquire.type
            t.issuedAt = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

            when (TicketType.values().find { it.name == ticketsToAcquire.type }!!) {
                TicketType.Ordinary -> {
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
}