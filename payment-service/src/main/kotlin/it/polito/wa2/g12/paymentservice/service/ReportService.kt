package it.polito.wa2.g12.paymentservice.service

import it.polito.wa2.g12.paymentservice.dto.DataRangeDTO
import it.polito.wa2.g12.paymentservice.dto.ReportDTO

interface ReportService {
    suspend fun getGlobalReport(dataRange: DataRangeDTO): ReportDTO
    suspend fun getUserReport(dataRange: DataRangeDTO, username: String): ReportDTO
}