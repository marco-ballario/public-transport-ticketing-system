package it.polito.wa2.g12.reportservice.dto

import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionDTO(
    val id: Long,
    val amount: BigDecimal,
    val username: String,
    val issuedAt: LocalDateTime,
)