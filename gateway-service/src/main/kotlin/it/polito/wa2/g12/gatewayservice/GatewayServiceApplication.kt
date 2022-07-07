package it.polito.wa2.g12.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class GatewayServiceApplication

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}
