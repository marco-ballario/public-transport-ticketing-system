package it.polito.wa2.g12.catalogueservice.repository

import it.polito.wa2.g12.catalogueservice.entity.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    fun findAllByUsername(@Param("username") username: String): Flow<Order>
    suspend fun findByUsernameAndId(
        @Param("username") username: String,
        @Param("id") orderId: Long
    ): Order?
}