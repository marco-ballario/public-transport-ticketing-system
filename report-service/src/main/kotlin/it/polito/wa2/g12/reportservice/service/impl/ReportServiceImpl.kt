package it.polito.wa2.g12.reportservice.service.impl

import it.polito.wa2.g12.reportservice.dto.DataRangeDTO
import it.polito.wa2.g12.reportservice.service.Report
import it.polito.wa2.g12.reportservice.service.ReportService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ReportServiceImpl : ReportService {
    override suspend fun getGlobalReport(jwt: String, dataRange: DataRangeDTO): Flow<Report> {
        return WebClient
            .create("http://localhost:8080")
            .post()
            .uri("/report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}