package it.polito.wa2.g12.reportservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class ReportServiceApplication

fun main(args: Array<String>) {
    runApplication<ReportServiceApplication>(*args)
}
