package it.polito.wa2.g12.loginservice.service

import org.springframework.mail.SimpleMailMessage

interface EmailService {
    fun sendEmail(receiverEmail: String, receiverNickname: String, activationCode: String): SimpleMailMessage
}