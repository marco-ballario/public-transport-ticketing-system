package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.dto.TimePeriodDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO
import it.polito.wa2.g12.paymentservice.service.impl.ReportServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

// These are the controllers called by the report service
// to generate the company report and single travelers reports
@RestController
@RequestMapping("/admin")
class ReportController(val reportService: ReportServiceImpl) {

    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(@RequestBody dataRange: TimePeriodDTO): ReportDTO {
        return reportService.getGlobalReport(dataRange)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun userReport(
        @PathVariable username: String,
        @RequestBody dataRange: TimePeriodDTO
    ): ReportDTO {
        return reportService.getUserReport(dataRange, username)
    }
}