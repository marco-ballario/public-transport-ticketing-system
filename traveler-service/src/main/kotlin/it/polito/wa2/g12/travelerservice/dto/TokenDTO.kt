package it.polito.wa2.g12.travelerservice.dto

data class TokenDTO(val sub: String, val iat: String, val exp: String, val roles: String)
