package it.polito.wa2.g12.paymentservice.service

import it.polito.wa2.g12.paymentservice.dto.GlobalReportDTO
import it.polito.wa2.g12.paymentservice.dto.TimePeriodDTO
import it.polito.wa2.g12.paymentservice.dto.UserReportDTO
import kotlinx.coroutines.flow.Flow

interface ReportService {
    suspend fun getGlobalReport(dataRange: TimePeriodDTO, jwt: String): Flow<GlobalReportDTO>
    suspend fun getUserReport(dataRange: TimePeriodDTO, username: String, jwt: String): Flow<UserReportDTO>
}