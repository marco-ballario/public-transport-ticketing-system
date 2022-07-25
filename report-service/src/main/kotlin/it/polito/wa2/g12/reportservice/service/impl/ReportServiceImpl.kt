package it.polito.wa2.g12.reportservice.service.impl

import it.polito.wa2.g12.reportservice.dto.TimePeriodDTO
import it.polito.wa2.g12.reportservice.dto.ReportDTO
import it.polito.wa2.g12.reportservice.service.ReportService
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ReportServiceImpl : ReportService {

    override suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): ReportDTO {
        return WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/admin/report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }

    override suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): ReportDTO {
        return WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/admin/report/$username")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}