package it.polito.wa2.g12.loginservice.repository

import it.polito.wa2.g12.loginservice.entity.RoleEntity
import it.polito.wa2.g12.loginservice.security.Role
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<RoleEntity, Long> {
    fun existsByRole(role: Role): Boolean
    fun findByRole(role: Role): RoleEntity?
}