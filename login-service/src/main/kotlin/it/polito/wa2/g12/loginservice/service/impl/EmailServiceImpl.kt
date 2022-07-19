package it.polito.wa2.g12.loginservice.service.impl

import it.polito.wa2.g12.loginservice.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailServiceImpl : EmailService {

    @Value("\${spring.mail.username}")
    lateinit var senderName: String

    @Autowired
    lateinit var emailSender: JavaMailSender

    override fun sendEmail(receiverEmail: String, receiverNickname: String, activationCode: String): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.setFrom(senderName)
        message.setTo(receiverEmail)
        message.setSubject("Verify your account")
        message.setText(
            "Dear $receiverNickname,\n" +
                    "Your activation code is: $activationCode\n" +
                    "Use it within the next 24 hours to complete your registration.\n" +
                    "Have a nice day!\n\n" +
                    "- Group 12"
        )
        emailSender.send(message)
        return message
    }

}