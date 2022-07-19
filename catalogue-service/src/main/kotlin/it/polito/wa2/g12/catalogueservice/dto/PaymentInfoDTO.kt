package it.polito.wa2.g12.catalogueservice.dto

class PaymentInfoDTO(
    val quantity: Int,
    val ticket_id: Long,
    val card_number: String,
    val card_expiration: String,
    val card_cvv: Int,
    val card_holder: String
)