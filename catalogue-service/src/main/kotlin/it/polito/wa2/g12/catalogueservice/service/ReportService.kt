package it.polito.wa2.g12.catalogueservice.service

import it.polito.wa2.g12.catalogueservice.dto.PurchasesStatsDTO

interface ReportService {
    suspend fun getOrderInfo(ordersID: List<Int>, jwt: String): PurchasesStatsDTO
}