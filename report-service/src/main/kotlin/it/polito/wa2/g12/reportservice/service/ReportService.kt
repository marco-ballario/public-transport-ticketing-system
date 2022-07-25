package it.polito.wa2.g12.reportservice.service

import it.polito.wa2.g12.reportservice.dto.TimePeriodDTO
import it.polito.wa2.g12.reportservice.dto.ReportDTO

interface ReportService {
    suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): ReportDTO
    suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): ReportDTO
}