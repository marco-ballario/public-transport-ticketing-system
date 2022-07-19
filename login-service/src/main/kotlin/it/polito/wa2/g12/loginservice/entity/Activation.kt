package it.polito.wa2.g12.loginservice.entity

import it.polito.wa2.g12.loginservice.dto.ActivationDTO
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "activations")
class Activation(
    @OneToOne
    var user: User,
    @Column(unique = true, nullable = false)
    var email: String,
    @Column
    var activationCode: String,
) {
    @Id
    @Column
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var provisionalId: UUID? = null

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var deadline: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(24))

    @Column
    var attemptCounter: Int = 5
}

fun Activation.toDTO() = ActivationDTO(provisionalId!!, email)
