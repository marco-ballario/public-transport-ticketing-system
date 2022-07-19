package it.polito.wa2.g12.travelerservice.repositories

import it.polito.wa2.g12.travelerservice.entities.UserDetails
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserDetailsRepository : CrudRepository<UserDetails, Long> {
    fun findByName(name: String): Optional<UserDetails>

    @Query("SELECT DISTINCT u.name FROM UserDetails u")
    fun findAllTravelers(): List<String>
}