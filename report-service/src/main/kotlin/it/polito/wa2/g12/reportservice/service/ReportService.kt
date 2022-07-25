package it.polito.wa2.g12.reportservice.service

import it.polito.wa2.g12.reportservice.dto.DataRangeDTO
import kotlinx.coroutines.flow.Flow

interface ReportService {
    suspend fun getGlobalReport(jwt: String, dataRange: DataRangeDTO): Flow<String>
    suspend fun getUserReport(jwt: String, dataRange: DataRangeDTO, username: String): Flow<String>
}