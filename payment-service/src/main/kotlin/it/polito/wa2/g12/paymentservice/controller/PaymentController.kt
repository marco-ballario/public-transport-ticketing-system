package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.dto.DataRangeDTO
import it.polito.wa2.g12.paymentservice.dto.TransactionDTO
import it.polito.wa2.g12.paymentservice.service.Report
import it.polito.wa2.g12.paymentservice.service.impl.PaymentServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    @PostMapping("/global_report")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: DataRangeDTO
    ): Flow<Report> {
        return paymentService.getGlobalReport(dataRange)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun userReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: DataRangeDTO,
        @PathVariable username: String
    ): Flow<Report> {
        return paymentService.getUserReport(dataRange, username)
    }
}