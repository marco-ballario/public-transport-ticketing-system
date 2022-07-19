package it.polito.wa2.g12.loginservice

import it.polito.wa2.g12.loginservice.entity.Activation
import it.polito.wa2.g12.loginservice.entity.User
import it.polito.wa2.g12.loginservice.entity.toDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class DTOUnitTests {

    @Test
    fun userToDTOTest() {
        val user = User("test@test.com", "test", "test", false)
        user.userId = 1
        val userDTO = user.toDTO()
        Assertions.assertEquals(1, userDTO.userId)
        Assertions.assertEquals("test", userDTO.nickname)
        Assertions.assertEquals("test@test.com", userDTO.email)
    }

    @Test
    fun activationToDTOTest() {
        val user = User("test@test.com", "test", "test", false)
        val activation = Activation(user, "test@test.com", "123456")
        val uuid = UUID.fromString("eda6bff4-cc1e-46be-80fe-b5a59fcc75e3")
        activation.provisionalId = uuid
        val activationDTO = activation.toDTO()
        Assertions.assertEquals(uuid, activationDTO.provisional_id)
        Assertions.assertEquals("test@test.com", activationDTO.email)
    }

}