package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import it.polito.wa2.g12.paymentservice.service.impl.PaymentServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class PaymentController(val paymentService: PaymentServiceImpl) {

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    fun getAllTransactions(principal: Principal): Flow<TransactionDTO> {
        return paymentService.getAllUserTransactions(principal.name)
    }

    @GetMapping("/admin/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getUserTransactions(): Flow<TransactionDTO> {
        return paymentService.getAllTransactions()
    }
}