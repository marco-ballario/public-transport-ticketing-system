package it.polito.wa2.g12.reportservice.controller

import it.polito.wa2.g12.reportservice.dto.GlobalReportDTO
import it.polito.wa2.g12.reportservice.dto.TimePeriodDTO
import it.polito.wa2.g12.reportservice.dto.UserReportDTO
import it.polito.wa2.g12.reportservice.service.impl.ReportServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class ReportController(val reportService: ReportServiceImpl) {

    // Use a JSON like this one to test this endpoint:
    // {"start_date": "2022-04-12 12:00:00", "end_date": "2022-06-01 18:00:00"}
    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: TimePeriodDTO
    ): GlobalReportDTO {
        return reportService.getGlobalReport(authorizationHeader, dataRange)
    }

    // Use a JSON like this one to test this endpoint:
    // {"start_date": "2022-04-12 12:00:00", "end_date": "2022-06-01 18:00:00"}
    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(
        @PathVariable username: String,
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: TimePeriodDTO
    ): UserReportDTO {
        return reportService.getUserReport(authorizationHeader, dataRange, username)
    }
}