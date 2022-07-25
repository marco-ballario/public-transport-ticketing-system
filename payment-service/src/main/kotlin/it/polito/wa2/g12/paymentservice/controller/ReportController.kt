package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.dto.DataRangeDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO
import it.polito.wa2.g12.paymentservice.service.impl.PaymentServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class ReportController(val paymentService: PaymentServiceImpl) {

    @PostMapping("/global_report")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(@RequestBody dataRange: DataRangeDTO): ReportDTO {
        return paymentService.getGlobalReport(dataRange)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun userReport(
        @PathVariable username: String,
        @RequestBody dataRange: DataRangeDTO
    ): ReportDTO {
        return paymentService.getUserReport(dataRange, username)
    }
}