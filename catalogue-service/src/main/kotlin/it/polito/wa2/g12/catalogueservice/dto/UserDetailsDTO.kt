package it.polito.wa2.g12.catalogueservice.dto

data class UserDetailsDTO(
    val username: String,
    val roles: List<String>
)