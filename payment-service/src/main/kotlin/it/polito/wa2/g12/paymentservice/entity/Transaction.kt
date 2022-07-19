package it.polito.wa2.g12.paymentservice.entity

import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transactions")
class Transaction(
    @Column
    var orderId: Int,
    @Column
    var username: String,
    @Column
    var amount: BigDecimal,
    @Column
    var issuedAt: LocalDateTime,
    @Column
    var status: String
) {
    @Id
    var id: Long? = null
}

fun Transaction.toDTO() = TransactionDTO(id!!, username, amount, issuedAt, orderId, status)