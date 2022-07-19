package it.polito.wa2.g12.catalogueservice.repository

import it.polito.wa2.g12.catalogueservice.entity.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    @Query(
        """
        SELECT *
        FROM orders
    """
    )
    fun findAllOrders(): Flow<Order>

    @Query(
        """
        SELECT *
        FROM orders
        WHERE username=:username
    """
    )
    fun findAllUserOrders(@Param("username") username: String): Flow<Order>

    @Query(
        """
        SELECT *
        FROM orders
        WHERE username = :username
        AND id = :id
    """
    )
    suspend fun findUserOrderById(
        @Param("username") username: String,
        @Param("id") orderId: Long
    ): Order?
}