package it.polito.wa2.g12.paymentservice.service.impl

import it.polito.wa2.g12.paymentservice.dto.DataRangeDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO
import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import it.polito.wa2.g12.paymentservice.entity.toDTO
import it.polito.wa2.g12.paymentservice.repository.TransactionRepository
import it.polito.wa2.g12.paymentservice.service.PaymentService
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PaymentServiceImpl : PaymentService {
    @Autowired
    lateinit var transactionRepository: TransactionRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun getAllTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAllTransactions().map { it.toDTO() }
    }

    override fun getAllUserTransactions(username: String): Flow<TransactionDTO> {
        return transactionRepository.findAllUserTransactions(username).map { it.toDTO() }
    }

    override suspend fun getGlobalReport(dataRange: DataRangeDTO): ReportDTO {
        val initData = LocalDateTime.parse(dataRange.initialData, formatter)
        val finalData = LocalDateTime.parse(dataRange.finalData, formatter)
        val transactionList = transactionRepository.findAllTransactions().filter {
            it.status == "SUCCESS" &&
            it.issuedAt.isAfter(initData) &&
            it.issuedAt.isBefore(finalData)
        }.map { it.toDTO() }
        return ReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            0
        )
    }

    override suspend fun getUserReport(dataRange: DataRangeDTO, username: String): ReportDTO {
        val initData = LocalDateTime.parse(dataRange.initialData, formatter)
        val finalData = LocalDateTime.parse(dataRange.finalData, formatter)
        val transactionList = transactionRepository.findAllTransactions().filter {
            it.username == username &&
            it.status == "SUCCESS" &&
            it.issuedAt.isAfter(initData) &&
            it.issuedAt.isBefore(finalData)
        }.map { it.toDTO() }
        return ReportDTO(
            transactionList.count(),
            transactionList.toList().sumOf { it.amount }.toFloat(),
            0
        )
    }
}