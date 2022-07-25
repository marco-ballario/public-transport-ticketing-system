package it.polito.wa2.g12.paymentservice.service.impl

import it.polito.wa2.g12.paymentservice.dto.TimePeriodDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO
import it.polito.wa2.g12.paymentservice.entity.toDTO
import it.polito.wa2.g12.paymentservice.repository.TransactionRepository
import it.polito.wa2.g12.paymentservice.service.ReportService
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ReportServiceImpl : ReportService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun getGlobalReport(dataRange: TimePeriodDTO): ReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.status == "SUCCESSFUL" &&
            it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
            it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        return ReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            0
        )
    }

    override suspend fun getUserReport(dataRange: TimePeriodDTO, username: String): ReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.username == username &&
            it.status == "SUCCESSFUL" &&
            it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
            it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        return ReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            0
        )
    }

}