package it.polito.wa2.g12.loginservice

import it.polito.wa2.g12.loginservice.dto.RegistrationDTO
import it.polito.wa2.g12.loginservice.repository.ActivationRepository
import it.polito.wa2.g12.loginservice.repository.RoleRepository
import it.polito.wa2.g12.loginservice.repository.UserRepository
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.atomic.AtomicInteger

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RateLimiterTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Test
    @Order(1)
    fun rateLimiterTests() {
        Thread.sleep(2000)
        val baseUrl = "http://localhost:$port"
        val countWrong = AtomicInteger()
        val count = AtomicInteger()

        // tests wrong requests because are faster
        val wrongReq = HttpEntity(RegistrationDTO("somename", "1234", "me@email.com"))
        val tl = mutableListOf<Thread>()
        for (i in 1..16) {
            tl.add(Thread {
                val wrongRes = restTemplate.postForEntity<Unit>("$baseUrl/user/register", wrongReq)
                if (wrongRes.statusCode == HttpStatus.TOO_MANY_REQUESTS)
                    countWrong.incrementAndGet()
                else
                    count.incrementAndGet()
            })
        }
        tl.forEach { it.start() }
        tl.forEach { it.join() }
        assert(count.get() == 10)
        assert(countWrong.get() == 6)
    }
}