package it.polito.wa2.g12.paymentservice.service.impl

import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import it.polito.wa2.g12.paymentservice.entity.toDTO
import it.polito.wa2.g12.paymentservice.repository.TransactionRepository
import it.polito.wa2.g12.paymentservice.service.PaymentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl : PaymentService {
    @Autowired
    lateinit var transactionRepository: TransactionRepository

    override fun getAllTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAllTransactions().map { it.toDTO() }
    }

    override fun getAllUserTransactions(username: String): Flow<TransactionDTO> {
        return transactionRepository.findAllUserTransactions(username).map { it.toDTO() }
    }
}