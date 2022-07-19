package it.polito.wa2.g12.bankservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class BankServiceApplication

fun main(args: Array<String>) {
    runApplication<BankServiceApplication>(*args)
}
