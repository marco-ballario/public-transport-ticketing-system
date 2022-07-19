package it.polito.wa2.g12.catalogueservice.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BillingMessage(
    @JsonProperty("order_id")
    val order_id: Int,
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
    @JsonProperty("username")
    val username: String,
    @JsonProperty("jwt")
    val jwt: String,
)
