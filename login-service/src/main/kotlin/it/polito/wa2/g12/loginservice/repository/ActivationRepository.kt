package it.polito.wa2.g12.loginservice.repository

import it.polito.wa2.g12.loginservice.entity.Activation
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ActivationRepository : CrudRepository<Activation, UUID>
