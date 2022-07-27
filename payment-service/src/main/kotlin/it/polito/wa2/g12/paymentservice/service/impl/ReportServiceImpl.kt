package it.polito.wa2.g12.paymentservice.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g12.paymentservice.dto.*
import it.polito.wa2.g12.paymentservice.entity.toDTO
import it.polito.wa2.g12.paymentservice.repository.TransactionRepository
import it.polito.wa2.g12.paymentservice.service.ReportService
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ReportServiceImpl : ReportService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun getGlobalReport(dataRange: TimePeriodDTO, jwt: String): GlobalReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.status == "SUCCESSFUL" &&
            it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
            it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        val response: String = WebClient
            .create("http://localhost:8083")
            .post()
            .uri("/admin/report")
            .header("Authorization", jwt)
            .bodyValue(transactionList.map { it.orderId }.toList())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val response2: String = WebClient
            .create("http://localhost:8087")
            .post()
            .uri("/admin/report/transits")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        val percentages = ob.readValue(response, PurchasesStatsDTO::class.java)
        val transits = ob.readValue(response2, TransitsStatsDTO::class.java)
        return GlobalReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            transits.transits,
            transactionList.toList().sumOf { it.amount }.toFloat() / percentages.ticketsNumber,
            percentages.percOrdinaryTickets.toFloat(),
            percentages.percTravelerCards.toFloat(),
            transits.percOrdinaryTransits,
            transits.percTravelerCardsTransits,
            percentages.ticketsNumber
        )
    }

    override suspend fun getUserReport(dataRange: TimePeriodDTO, username: String, jwt: String): UserReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.username == username &&
            it.status == "SUCCESSFUL" &&
            it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
            it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        val response: String = WebClient
            .create("http://localhost:8083")
            .post()
            .uri("/admin/report")
            .header("Authorization", jwt)
            .bodyValue(transactionList.map { it.orderId }.toList())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val response2: String = WebClient
            .create("http://localhost:8087")
            .post()
            .uri("/admin/report/$username/transits")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        val percentages = ob.readValue(response, PurchasesStatsDTO::class.java)
        val transits = ob.readValue(response2, TransitsStatsDTO::class.java)
        return UserReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            transits.transits,
            transactionList.toList().sumOf { it.amount }.toFloat() / percentages.ticketsNumber,
            transactionList.toList().minOf { it.amount }.toFloat(),
            transactionList.toList().maxOf { it.amount }.toFloat(),
            percentages.percOrdinaryTickets.toFloat(),
            percentages.percTravelerCards.toFloat(),
            transits.percOrdinaryTransits,
            transits.percTravelerCardsTransits,
            percentages.ticketsNumber
        )
    }

}