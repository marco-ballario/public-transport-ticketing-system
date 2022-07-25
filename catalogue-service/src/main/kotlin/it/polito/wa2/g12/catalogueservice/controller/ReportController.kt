package it.polito.wa2.g12.catalogueservice.controller

import it.polito.wa2.g12.catalogueservice.dto.PercentagesDTO
import it.polito.wa2.g12.catalogueservice.service.impl.ReportServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

// These are the controllers called by the report service
// to generate the company report and single travelers reports
@RestController
@RequestMapping("/admin")
class ReportController(val reportService: ReportServiceImpl) {

    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    suspend fun globalReport(@RequestBody ordersID: List<Int>): Flow<PercentagesDTO> {
        return reportService.getOrderInfo(ordersID)
    }
}