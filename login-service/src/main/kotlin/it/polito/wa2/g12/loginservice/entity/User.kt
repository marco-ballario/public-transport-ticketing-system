package it.polito.wa2.g12.loginservice.entity

import it.polito.wa2.g12.loginservice.dto.UserDTO
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = false, unique = true)
    var nickname: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false)
    var validated: Boolean = false,
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @OnDelete(action = OnDeleteAction.CASCADE)
    var roles: MutableSet<RoleEntity> = mutableSetOf<RoleEntity>(),
) {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var userId: Long? = null
}

fun User.toDTO() = UserDTO(userId!!, nickname, email)
