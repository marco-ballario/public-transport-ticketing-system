package it.polito.wa2.g12.catalogueservice.controller

import it.polito.wa2.g12.catalogueservice.dto.PurchasesStatsDTO
import it.polito.wa2.g12.catalogueservice.service.impl.ReportServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

// These are the controllers called by the report service
// to generate the company report and single travelers reports
@RestController
@RequestMapping("/admin")
class ReportController(val reportService: ReportServiceImpl) {

    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    suspend fun globalReport(
        @RequestBody ordersID: List<Int>,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): PurchasesStatsDTO {
        return reportService.getOrderInfo(ordersID, authorizationHeader)
    }
}