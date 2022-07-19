package it.polito.wa2.g12.paymentservice.service

import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import kotlinx.coroutines.flow.Flow

interface PaymentService {
    fun getAllTransactions(): Flow<TransactionDTO>
    fun getAllUserTransactions(username: String): Flow<TransactionDTO>
}