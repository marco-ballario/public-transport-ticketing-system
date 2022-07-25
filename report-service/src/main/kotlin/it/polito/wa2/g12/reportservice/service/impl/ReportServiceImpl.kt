package it.polito.wa2.g12.reportservice.service.impl

import it.polito.wa2.g12.reportservice.dto.DataRangeDTO
import it.polito.wa2.g12.reportservice.service.ReportService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.kotlin.core.publisher.toMono

@Service
class ReportServiceImpl : ReportService {
    override suspend fun getGlobalReport(jwt: String, dataRange: DataRangeDTO): Flow<String> {
        val response : String = WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/global_report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        return response.toMono().asFlow()
    }

    override suspend fun getUserReport(jwt: String, dataRange: DataRangeDTO, username: String): Flow<String> {
        val response : String = WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/report/$username")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        return response.toMono().asFlow()
    }
}