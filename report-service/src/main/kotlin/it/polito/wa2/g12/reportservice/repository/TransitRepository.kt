package it.polito.wa2.g12.reportservice.repository

import it.polito.wa2.g12.reportservice.entity.Transit
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TransitRepository : CoroutineCrudRepository<Transit, Long> {
}