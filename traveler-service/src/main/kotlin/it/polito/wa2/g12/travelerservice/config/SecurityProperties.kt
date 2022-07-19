package it.polito.wa2.g12.travelerservice.config

import io.jsonwebtoken.io.Decoders
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
class SecurityProperties(
    secretKey: String,
    val tokenPrefix: String,
    val headerString: String,
) {
    val secret: ByteArray = Decoders.BASE64.decode(secretKey)
}