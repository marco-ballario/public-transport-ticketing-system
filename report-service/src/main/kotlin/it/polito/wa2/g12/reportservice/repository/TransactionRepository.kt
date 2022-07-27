package it.polito.wa2.g12.reportservice.repository

import it.polito.wa2.g12.reportservice.entity.Transaction
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
}