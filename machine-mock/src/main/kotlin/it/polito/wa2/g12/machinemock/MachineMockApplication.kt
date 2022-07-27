package it.polito.wa2.g12.machinemock

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import javax.crypto.SecretKey

@EnableEurekaClient
@SpringBootApplication
class MachineMockApplication {
	@Value("\${ticketKey}")
	lateinit var stringKey: String

	@Bean
	fun secretKey(): SecretKey {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(stringKey))
	}
}

fun main(args: Array<String>) {
	runApplication<MachineMockApplication>(*args)
}
