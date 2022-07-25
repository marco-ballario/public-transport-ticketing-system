package it.polito.wa2.g12.paymentservice.service

import it.polito.wa2.g12.paymentservice.dto.DataRangeDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO
import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import kotlinx.coroutines.flow.Flow

interface PaymentService {
    fun getAllTransactions(): Flow<TransactionDTO>
    fun getAllUserTransactions(username: String): Flow<TransactionDTO>
    suspend fun getGlobalReport(dataRange: DataRangeDTO): ReportDTO
    suspend fun getUserReport(dataRange: DataRangeDTO, username: String): ReportDTO
}