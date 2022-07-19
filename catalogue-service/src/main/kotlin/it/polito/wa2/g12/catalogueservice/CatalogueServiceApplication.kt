package it.polito.wa2.g12.catalogueservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class CatalogueServiceApplication

fun main(args: Array<String>) {
    runApplication<CatalogueServiceApplication>(*args)
}
