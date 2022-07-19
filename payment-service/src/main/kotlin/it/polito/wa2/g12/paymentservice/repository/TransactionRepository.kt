package it.polito.wa2.g12.paymentservice.repository

import it.polito.wa2.g12.paymentservice.entity.Transaction
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
    @Query(
        """
        SELECT *
        FROM transactions
    """
    )
    fun findAllTransactions(): Flow<Transaction>

    @Query(
        """
        SELECT *
        FROM transactions
        WHERE username=:username
    """
    )
    fun findAllUserTransactions(@Param("username") username: String): Flow<Transaction>
}