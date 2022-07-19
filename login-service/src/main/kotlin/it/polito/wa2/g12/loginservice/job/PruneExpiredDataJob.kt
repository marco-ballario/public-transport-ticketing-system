package it.polito.wa2.g12.loginservice.job

import it.polito.wa2.g12.loginservice.repository.ActivationRepository
import it.polito.wa2.g12.loginservice.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@EnableScheduling
class PruneExpiredDataJob {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Scheduled(initialDelayString = "\${job.initial.delay}", fixedDelayString = "\${job.execution.rate}")
    fun pruneExpiredRegistrationData() {
        val expiredRows = activationRepository.findAll().filter {
            it.deadline.before(java.sql.Timestamp.valueOf(LocalDateTime.now()))
        }
        expiredRows.forEach {
            activationRepository.deleteById(it.provisionalId!!)
            userRepository.deleteById(it.user.userId!!)
        }
    }
}