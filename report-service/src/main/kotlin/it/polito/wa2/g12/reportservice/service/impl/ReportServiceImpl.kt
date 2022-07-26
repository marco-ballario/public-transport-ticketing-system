package it.polito.wa2.g12.reportservice.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g12.reportservice.dto.GlobalReportDTO
import it.polito.wa2.g12.reportservice.dto.TimePeriodDTO
import it.polito.wa2.g12.reportservice.dto.UserReportDTO
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

    override suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): Flow<GlobalReportDTO> {
        val response: String = WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/admin/report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        return ob.readValue(response, object : TypeReference<List<GlobalReportDTO>>(){})[0].toMono().asFlow()
    }

    override suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): Flow<UserReportDTO> {
        val response: String = WebClient
            .create("http://localhost:8084")
            .post()
            .uri("/admin/report/$username")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        return ob.readValue(response, object : TypeReference<List<UserReportDTO>>(){})[0].toMono().asFlow()
    }
}