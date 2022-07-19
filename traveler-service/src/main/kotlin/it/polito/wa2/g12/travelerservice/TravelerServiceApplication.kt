package it.polito.wa2.g12.travelerservice

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import javax.crypto.SecretKey

@EnableEurekaClient
@SpringBootApplication(exclude = [(RepositoryRestMvcAutoConfiguration::class)])
class TravelerServiceApplication {
    @Value("\${ticketKey}")
    lateinit var stringKey: String

    @Bean
    fun secretKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(stringKey))
    }
}

fun main(args: Array<String>) {
    configureApplication(SpringApplicationBuilder()).run(*args)
}

fun configureApplication(builder: SpringApplicationBuilder): SpringApplicationBuilder {
    return builder.sources(TravelerServiceApplication::class.java)
}