package it.polito.wa2.g12.paymentservice.controller

import it.polito.wa2.g12.paymentservice.dto.GlobalReportDTO
import it.polito.wa2.g12.paymentservice.dto.TimePeriodDTO
import it.polito.wa2.g12.paymentservice.dto.UserReportDTO
import it.polito.wa2.g12.paymentservice.service.impl.ReportServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

// These are the controllers called by the report service
// to generate the company report and single travelers reports
@RestController
@RequestMapping("/admin")
class ReportController(val reportService: ReportServiceImpl) {

    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: TimePeriodDTO
    ): GlobalReportDTO {
        return reportService.getGlobalReport(dataRange, authorizationHeader)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    suspend fun userReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable username: String,
        @RequestBody dataRange: TimePeriodDTO
    ): UserReportDTO {
        return reportService.getUserReport(dataRange, username, authorizationHeader)
    }
}