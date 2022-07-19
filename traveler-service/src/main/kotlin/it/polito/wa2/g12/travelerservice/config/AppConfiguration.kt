package it.polito.wa2.g12.travelerservice.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    SecurityProperties::class
)
class AppConfiguration