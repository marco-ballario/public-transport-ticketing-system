package it.polito.wa2.g12.reportservice.service

import it.polito.wa2.g12.reportservice.dto.GlobalReportDTO
import it.polito.wa2.g12.reportservice.dto.TimePeriodDTO
import it.polito.wa2.g12.reportservice.dto.UserReportDTO
import kotlinx.coroutines.flow.Flow

interface ReportService {
    suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): Flow<GlobalReportDTO>
    suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): Flow<UserReportDTO>
}