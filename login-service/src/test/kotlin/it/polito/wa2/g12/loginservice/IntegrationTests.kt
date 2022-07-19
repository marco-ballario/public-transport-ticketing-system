package it.polito.wa2.g12.loginservice

import it.polito.wa2.g12.loginservice.dto.*
import it.polito.wa2.g12.loginservice.repository.ActivationRepository
import it.polito.wa2.g12.loginservice.repository.RoleRepository
import it.polito.wa2.g12.loginservice.repository.UserRepository
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationTests {

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
    fun registerUserTest() {
        val baseUrl = "http://localhost:$port"

        // sends a request with a weak password
        val wrongReq = HttpEntity(RegistrationDTO("somename", "1234", "me@email.com"))
        val wrongRes = restTemplate.postForEntity<Unit>("$baseUrl/user/register", wrongReq)
        assert(wrongRes.statusCode == HttpStatus.BAD_REQUEST)

        // sends a request with a wrong email
        val wrongReq1 = HttpEntity(RegistrationDTO("somename", "Secret!Password1", "meemail"))
        val wrongRes1 = restTemplate.postForEntity<Unit>("$baseUrl/user/register", wrongReq1)
        assert(wrongRes1.statusCode == HttpStatus.BAD_REQUEST)

        // sends a request with a empty nickname
        val wrongReq2 = HttpEntity(RegistrationDTO("", "Secret!Password1", "me@email.com"))
        val wrongRes2 = restTemplate.postForEntity<Unit>("$baseUrl/user/register", wrongReq2)
        assert(wrongRes2.statusCode == HttpStatus.BAD_REQUEST)

        // sends a request with a strong password
        val rightReq = HttpEntity(RegistrationDTO("somename", "Secret!Password1", "me@email.com"))
        val rightRes = restTemplate.postForEntity<ActivationDTO>("$baseUrl/user/register", rightReq)
        println(rightRes.statusCode)
        assert(rightRes.statusCode == HttpStatus.ACCEPTED)

        // deletes created records from the db
        val act = activationRepository.findById(rightRes.body!!.provisional_id).get()
        val userId = act.user.userId!!
        activationRepository.deleteById(act.provisionalId!!)
        userRepository.deleteById(userId)
    }

    @Test
    fun activateUserTest() {
        val baseUrl = "http://localhost:$port"

        // sends a request with wrong provisional id and activation code
        val wrongReq = HttpEntity(TokenDTO("1234", "1234"))
        val wrongRes = restTemplate.postForEntity<Unit>("$baseUrl/user/validate", wrongReq)
        assert(wrongRes.statusCode == HttpStatus.NOT_FOUND)

        // registers a user in the db
        val registerReq = HttpEntity(RegistrationDTO("somename", "Secret!Password1", "me@email.com"))
        val registerRes = restTemplate.postForEntity<ActivationDTO>("$baseUrl/user/register", registerReq)
        assert(registerRes.statusCode == HttpStatus.ACCEPTED)

        // sends a request with correct provisional id and activation code
        val activationCode = activationRepository.findById(registerRes.body!!.provisional_id).get().activationCode
        val rightReq = HttpEntity(TokenDTO(registerRes.body!!.provisional_id.toString(), activationCode))
        val rightRes = restTemplate.postForEntity<UserDTO>("$baseUrl/user/validate", rightReq)
        assert(rightRes.statusCode == HttpStatus.CREATED)

        // deletes the one remaining record from the db
        val user = userRepository.findById(rightRes.body!!.userId).get()
        val roles = roleRepository.findAll()
        roles.forEach { roleEntity -> user.roles.remove(roleEntity) }
        roles.forEach { roleEntity -> roleEntity.users.remove(user) }
        userRepository.save(user)
        roleRepository.saveAll(roles)
        userRepository.deleteById(rightRes.body!!.userId)
    }

    @Test
    fun loginTests() {
        val baseUrl = "http://localhost:$port"

        val registerReq = HttpEntity(RegistrationDTO("somename2", "Secret!Password12", "me2@email.com"))
        val registerRes = restTemplate.postForEntity<ActivationDTO>("$baseUrl/user/register", registerReq)
        assert(registerRes.statusCode == HttpStatus.ACCEPTED)

        // sends a request with correct provisional id and activation code
        val activationCode = activationRepository.findById(registerRes.body!!.provisional_id).get().activationCode
        val rightReq = HttpEntity(TokenDTO(registerRes.body!!.provisional_id.toString(), activationCode))
        val rightRes = restTemplate.postForEntity<UserDTO>("$baseUrl/user/validate", rightReq)
        assert(rightRes.statusCode == HttpStatus.CREATED)

        val loginReq = HttpEntity(LoginDTO("somename2", "Secret!Password12"))
        val loginRes = restTemplate.postForEntity<String>("$baseUrl/user/login", loginReq)
        assert(loginRes.statusCode == HttpStatus.OK)
        assert(loginRes.body is String)

        val loginReq2 = HttpEntity(LoginDTO("somename2", "Secret!Password11111"))
        val loginRes2 = restTemplate.postForEntity<String>("$baseUrl/user/login", loginReq2)
        assert(loginRes2.statusCode == HttpStatus.BAD_REQUEST)
    }
}