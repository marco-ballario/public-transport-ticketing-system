package it.polito.wa2.g12.reportservice.controller

import it.polito.wa2.g12.reportservice.dto.DataRangeDTO
import it.polito.wa2.g12.reportservice.service.Report
import it.polito.wa2.g12.reportservice.service.impl.ReportServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(val reportservice: ReportServiceImpl) {
    // Use a JSON like this one to test this endpoint:
    // {"initialData": "2022-04-12 12:00:00", "finalData": "2022-06-1 18:00:00"}
    @GetMapping("/global/report")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: DataRangeDTO
    ): Flow<Report> {
        return reportservice.getGlobalReport(authorizationHeader, dataRange)
    }
}