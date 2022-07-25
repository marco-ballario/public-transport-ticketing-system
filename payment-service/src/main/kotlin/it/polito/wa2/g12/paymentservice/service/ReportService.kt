package it.polito.wa2.g12.paymentservice.service

import it.polito.wa2.g12.paymentservice.dto.TimePeriodDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO

interface ReportService {
    suspend fun getGlobalReport(dataRange: TimePeriodDTO): ReportDTO
    suspend fun getUserReport(dataRange: TimePeriodDTO, username: String): ReportDTO
}