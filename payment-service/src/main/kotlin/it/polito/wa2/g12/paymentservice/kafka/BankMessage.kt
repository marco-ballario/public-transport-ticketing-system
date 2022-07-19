package it.polito.wa2.g12.paymentservice.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BankMessage(
    @JsonProperty("transaction_id")
    val transaction_id: Long,
    @JsonProperty("price")
    val price: BigDecimal,
    @JsonProperty("ccn")
    val ccn: String,
    @JsonProperty("exp")
    val exp: String,
    @JsonProperty("cvv")
    val cvv: String,
    @JsonProperty("card_holder")
    val card_holder: String,
    @JsonProperty("jwt")
    val jwt: String,
)
