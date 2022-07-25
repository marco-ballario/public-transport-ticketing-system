package it.polito.wa2.g12.reportservice.controller

import it.polito.wa2.g12.reportservice.dto.DataRangeDTO
import it.polito.wa2.g12.reportservice.service.impl.ReportServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class ReportController(val reportservice: ReportServiceImpl) {

    // Use a JSON like this one to test this endpoint:
    // {"initialData": "2022-04-12 12:00:00", "finalData": "2022-06-01 18:00:00"}
    @PostMapping("/report/global")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: DataRangeDTO
    ): Flow<String> {
        return reportservice.getGlobalReport(authorizationHeader, dataRange)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(
        @PathVariable username: String,
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: DataRangeDTO
    ): Flow<String> {
        return reportservice.getUserReport(authorizationHeader, dataRange, username)
    }
}