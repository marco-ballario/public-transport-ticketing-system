package it.polito.wa2.g12.loginservice.dto

import it.polito.wa2.g12.loginservice.security.Role
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class AdminDTO(
    @field:NotEmpty @field:NotNull val username: String,
    @field:Size(min = 8) @field:NotNull var password: String,
    @field:Email @field:NotNull var email: String,
    val role : Role,
)
