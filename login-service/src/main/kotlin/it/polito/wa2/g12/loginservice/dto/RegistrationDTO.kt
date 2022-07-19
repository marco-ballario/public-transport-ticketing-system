package it.polito.wa2.g12.loginservice.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RegistrationDTO(
    @field:NotEmpty @field:NotNull val nickname: String,
    @field:Size(min = 8) @field:NotNull var password: String,
    @field:Email @field:NotNull var email: String
)
