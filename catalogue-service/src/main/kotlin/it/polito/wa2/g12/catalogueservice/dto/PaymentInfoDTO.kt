package it.polito.wa2.g12.catalogueservice.dto

class PaymentInfoDTO(
    val ticket_id: Long,
    val quantity: Int,
    val card_number: String,
    val card_expiration: String,
    val card_cvv: Int,
    val card_holder: String
)