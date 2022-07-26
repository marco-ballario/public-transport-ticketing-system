package it.polito.wa2.g12.catalogueservice.service

import it.polito.wa2.g12.catalogueservice.dto.PercentagesDTO

interface ReportService {
    suspend fun getOrderInfo(ordersID: List<Int>): PercentagesDTO
}