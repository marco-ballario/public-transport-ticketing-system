package it.polito.wa2.g12.reportservice.repository

import it.polito.wa2.g12.reportservice.entity.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
}