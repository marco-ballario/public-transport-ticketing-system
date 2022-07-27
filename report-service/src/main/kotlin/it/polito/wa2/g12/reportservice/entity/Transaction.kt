package it.polito.wa2.g12.reportservice.entity

import it.polito.wa2.g12.reportservice.dto.TransactionDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transactions")
class Transaction(
    @Column var amount: BigDecimal,
    @Column var username: String,
    @Column var issuedAt: LocalDateTime,
) {
    @Id var id: Long? = null
}

fun Transaction.toDTO() = TransactionDTO(id!!, amount, username, issuedAt)