package it.polito.wa2.g12.paymentservice.repository

import it.polito.wa2.g12.paymentservice.entity.Transaction
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
    fun findAllByUsername(@Param("username") username: String): Flow<Transaction>
}