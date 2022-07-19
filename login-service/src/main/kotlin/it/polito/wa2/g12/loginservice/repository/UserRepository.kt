package it.polito.wa2.g12.loginservice.repository

import it.polito.wa2.g12.loginservice.entity.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByNickname(nickname: String): User?
}
