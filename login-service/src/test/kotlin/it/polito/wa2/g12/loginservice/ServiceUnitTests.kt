package it.polito.wa2.g12.loginservice

import io.jsonwebtoken.Jwts
import it.polito.wa2.g12.loginservice.dto.RegistrationDTO
import it.polito.wa2.g12.loginservice.dto.TokenDTO
import it.polito.wa2.g12.loginservice.entity.RoleEntity
import it.polito.wa2.g12.loginservice.entity.User
import it.polito.wa2.g12.loginservice.repository.ActivationRepository
import it.polito.wa2.g12.loginservice.repository.RoleRepository
import it.polito.wa2.g12.loginservice.repository.UserRepository
import it.polito.wa2.g12.loginservice.security.Role
import it.polito.wa2.g12.loginservice.service.impl.EmailServiceImpl
import it.polito.wa2.g12.loginservice.service.impl.UserServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*
import javax.crypto.SecretKey

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceUnitTests {

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

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var userService: UserServiceImpl

    @Autowired
    lateinit var emailService: EmailServiceImpl

    @Autowired
    lateinit var secretKey: SecretKey

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder


    @Test
    fun isValidPwdTest() {
        val withSpacePwd = "Secret! Password1"
        Assertions.assertFalse(userService.isValidPwd(withSpacePwd))
        val missingDigitPwd = "Secret!Password"
        Assertions.assertFalse(userService.isValidPwd(missingDigitPwd))
        val missingLowerCaseLetterPwd = "SECRET!PASSWORD1"
        Assertions.assertFalse(userService.isValidPwd(missingLowerCaseLetterPwd))
        val missingUpperCaseLetterPwd = "secret!password1"
        Assertions.assertFalse(userService.isValidPwd(missingUpperCaseLetterPwd))
        val missingNotAlphanumCharPwd = "SecretPassword1"
        Assertions.assertFalse(userService.isValidPwd(missingNotAlphanumCharPwd))
        val goodPwd = "Secret!Password1"
        Assertions.assertTrue(userService.isValidPwd(goodPwd))
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun isValidEmailAndNicknameTest() {
        // checks that nickname and email are not yet present in the db
        val email = "1@email.com"
        val nickname = "somename1"
        Assertions.assertTrue(userService.isValidEmail(email))
        Assertions.assertTrue(userService.isValidNickname(nickname))

        // saves email and nickname in the db
        val reg = RegistrationDTO(nickname, "Secret!Password1", email)
        val res = userService.userReg(reg)

        // checks that nickname and email are now present in the db
        Assertions.assertFalse(userService.isValidEmail(email))
        Assertions.assertFalse(userService.isValidNickname(nickname))

        // deletes registration from the db
        val userId = activationRepository.findById(res.provisional_id).get().user.userId
        activationRepository.deleteById(res.provisional_id)
        userRepository.deleteById(userId!!)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun isValidProvisionalIdTest() {
        Assertions.assertTrue(userService.isValidProvisionalId("eda6bff4-cc1e-46be-80fe-b5a59fcc75e3"))
        Assertions.assertFalse(userService.isValidProvisionalId("abcdef"))
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun isValidActivationCodeTest() {
        Assertions.assertTrue(userService.isValidActivationCode("123456"))
        Assertions.assertFalse(userService.isValidActivationCode("abcdef"))
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun newActivationCodeTest() {
        Assertions.assertEquals(6, userService.newActivationCode().length)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun userRegTest() {
        // checks that the registration is not yet present in the db
        val email = "me2@email.com"
        val nickname = "somename2"
        val password = "Secret!Password1"
        Assertions.assertFalse(userRepository.existsByEmail(email))
        Assertions.assertFalse(userRepository.existsByNickname(nickname))

        // saves the registration in the db
        val reg = RegistrationDTO(nickname, password, email)
        val act = userService.userReg(reg)

        // checks that the registration has been saved in the db
        Assertions.assertTrue(userRepository.existsByEmail(email))
        Assertions.assertTrue(userRepository.existsByNickname(nickname))
        Assertions.assertTrue(activationRepository.existsById(act.provisional_id))

        // deletes registration from the db
        val userId = activationRepository.findById(act.provisional_id).get().user.userId
        activationRepository.deleteById(act.provisional_id)
        userRepository.deleteById(userId!!)
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun completedRegCorrectActivationCodeTest() {
        // Registers a user in the db
        val email = "test@email.com"
        val nickname = "test"
        val password = "Secret!Password3"
        val reg = RegistrationDTO(nickname, password, email)
        val act = userService.userReg(reg)
        println("REG DONE")
        // Submits the right activation code
        val activationCode = activationRepository.findById(act.provisional_id).get().activationCode
        println("ACT DONE")
        val tok = TokenDTO(act.provisional_id.toString(), activationCode)
        val res = userService.completedReg(tok)
        println("ACT DONE")
        Assertions.assertNotNull(res)
        Assertions.assertTrue(activationRepository.findById(act.provisional_id).isEmpty)
        Assertions.assertTrue(userRepository.findById(res!!.userId).get().validated)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (user in users) {
            user.roles.removeAll(roles)
        }
        for (role in roles) {
            role.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()


    }

    @Test
    fun completedRegProvisionalIdNotFoundTest() {
        // Submits an activation code for a provisional id not saved in the db
        val tok = TokenDTO("eda6bff4-cc1e-46be-80fe-b5a59fcc75e3", "123456")
        val res = userService.completedReg(tok)
        Assertions.assertNull(res)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun completedRegExpiredActivationCodeTest() {
        // Registers a user in the db
        val email = "me4@email.com"
        val nickname = "somename4"
        val password = "Secret!Password1"
        val reg = RegistrationDTO(nickname, password, email)
        val act = userService.userReg(reg)

        // Sets deadline to January 1, 1970, 00:00:00 GMT
        val actEntity = activationRepository.findById(act.provisional_id).get()
        actEntity.deadline = Date(0)
        activationRepository.save(actEntity)

        // Submits the right activation code
        val activationCode = activationRepository.findById(act.provisional_id).get().activationCode
        val tok = TokenDTO(act.provisional_id.toString(), activationCode)
        val res = userService.completedReg(tok)
        Assertions.assertNull(res)
        Assertions.assertTrue(activationRepository.findById(act.provisional_id).isEmpty)
        Assertions.assertFalse(userRepository.existsByEmail(act.email))
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun completedRegWrongActivationCodeTest() {
        // Registers a user in the db
        val email = "me5@email.com"
        val nickname = "somename5"
        val password = "Secret!Password1"
        val reg = RegistrationDTO(nickname, password, email)
        val act = userService.userReg(reg)

        // Submits five wrong activation codes
        val activationCode = "abcdef"
        val tok = TokenDTO(act.provisional_id.toString(), activationCode)
        val userId = activationRepository.findById(act.provisional_id).get().user.userId!!
        for (i in 4 downTo 1) {
            val res = userService.completedReg(tok)
            val attempts = activationRepository.findById(act.provisional_id).get().attemptCounter
            Assertions.assertNull(res)
            Assertions.assertEquals(attempts, i)
        }
        Assertions.assertNull(userService.completedReg(tok))
        Assertions.assertTrue(activationRepository.findById(act.provisional_id).isEmpty)
        Assertions.assertTrue(userRepository.findById(userId).isEmpty)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }

    @Test
    fun loginTest() {

        val email = "me@email.com"
        val nickname = "somename"
        val password = "Simple1Password!"
        val encodedpw = bCryptPasswordEncoder.encode(password)
        var user = User(email, nickname, encodedpw, true)
        user = userRepository.save(user)
        var role: RoleEntity
        if (roleRepository.existsByRole(Role.CUSTOMER)) {
            role = roleRepository.findByRole(Role.CUSTOMER)!!
        } else {
            role = roleRepository.save(RoleEntity(mutableSetOf(), Role.CUSTOMER))
        }
        role.users.add(user)
        user.roles.add(role)
        role = roleRepository.save(role)
        user = userRepository.save(user)
        // sends email and checks its content
        val token = userService.login(nickname, password)
        assert(token != null)
        val jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()
        val userId = jwtParser.parseClaimsJws(token).body.subject
        assert(userId == user.nickname)
        val tokenNull = userService.login(nickname, "Simple1Password£")
        assert(tokenNull == null)
        val tokenNull2 = userService.login("samename", password)
        assert(tokenNull2 == null)
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
    }

    @Test
    fun sendEmailTest() {
        val email = "me@email.com"
        val nickname = "somename"
        val activationCode = "123456"

        // sends email and checks its content
        Assertions.assertDoesNotThrow {
            val message = emailService.sendEmail(email, nickname, activationCode)
            Assertions.assertEquals(email, message.to!![0])
            Assertions.assertTrue(message.text!!.contains(activationCode))
            Assertions.assertTrue(message.text!!.contains(nickname))
        }
        // deletes registration from the db
        val users = userRepository.findAll()
        val roles = roleRepository.findAll()
        for (u in users) {
            u.roles.removeAll(roles)
        }
        for (r in roles) {
            r.users.removeAll(users)
        }
        userRepository.saveAll(users)
        roleRepository.saveAll(roles)
        userRepository.deleteAll()
        roleRepository.deleteAll()
    }
}