package it.polito.wa2.g12.gatewayservice

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@EnableEurekaClient
@SpringBootApplication
class GatewayServiceApplication {
    @Bean
    fun defaultCustomizer(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id).circuitBreakerConfig(
                    CircuitBreakerConfig.ofDefaults()
                ).timeLimiterConfig(
                    TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build()
                ).build()
            }
        }
    }
}

@RestController
class GatewayController {
    @GetMapping("/fallback")
    fun fallback(): Mono<String> {
        return Mono.just("The service responsible for processing the request is currently not responding.")
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}
