package it.polito.wa2.g12.transitservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class TransitServiceApplication

fun main(args: Array<String>) {
	runApplication<TransitServiceApplication>(*args)
}
