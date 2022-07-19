package it.polito.wa2.g12.catalogueservice.kafka

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionMessage(
    @JsonProperty("order_id")
    val order_id: Int,
    @JsonProperty("transaction_id")
    val transaction_id: Long?,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("jwt")
    val jwt: String,
)
