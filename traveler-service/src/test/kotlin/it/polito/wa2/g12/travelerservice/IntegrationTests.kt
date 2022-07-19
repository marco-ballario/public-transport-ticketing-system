package it.polito.wa2.g12.travelerservice

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

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

    @Value("\${jwt.secret-key}")
    lateinit var jwtSecretKey: String

    fun generateJwt(hoursOfDuration: Int, roles: List<String>): String? {
        val now = Calendar.getInstance()
        val exp = Calendar.getInstance()
        exp.add(Calendar.HOUR, hoursOfDuration)
        val claims = mapOf<String, Any>(
            "sub" to "test",
            "exp" to exp.time,
            "iat" to now.time,
            "roles" to roles
        )
        val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey))
        return Jwts.builder().setClaims(claims).signWith(secretKey).compact()
    }

    fun sendAuthAndNotAuthRequests(
        method: HttpMethod,
        endpoint: String,
        body: String,
        roles: List<String>,
        forbidden: Boolean
    ) {
        val baseUrl = "http://localhost:$port"

        // Creates a valid authorization bearer header
        val jwt = generateJwt(1, roles)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Authorization"] = "Bearer $jwt"

        // Not authenticated requests should be forbidden
        val notAuthReq = HttpEntity(body)
        val notAuthRes = restTemplate.exchange(baseUrl + endpoint, method, notAuthReq, String::class.java)
        assert(notAuthRes.statusCode == HttpStatus.FORBIDDEN)

        // Authenticated requests may not be forbidden according to the roles
        val authReq = HttpEntity<String>(body, headers)
        val authRes = restTemplate.exchange(baseUrl + endpoint, method, authReq, String::class.java)
        if (forbidden)
            assert(authRes.statusCode == HttpStatus.FORBIDDEN)
        else
            assert(authRes.statusCode != HttpStatus.FORBIDDEN)
    }

    @Test
    fun authorizationTests() {
        val allRoles = listOf("ADMIN", "CUSTOMER")
        val adminRole = listOf("ADMIN")
        val customerRole = listOf("CUSTOMER")

        val endpoints = listOf(
            listOf(HttpMethod.GET, "/my/profile", "", allRoles, false),
            listOf(HttpMethod.GET, "/my/tickets", "", allRoles, false),
            listOf(HttpMethod.GET, "/admin/travelers", "", allRoles, false),
            listOf(HttpMethod.GET, "/admin/traveler/1/profile", "", allRoles, false),
            listOf(HttpMethod.GET, "/admin/traveler/1/tickets", "", allRoles, false),
            listOf(HttpMethod.GET, "/admin/traveler/1/profile", "", adminRole, false),
            listOf(HttpMethod.GET, "/admin/traveler/1/tickets", "", adminRole, false),
            listOf(HttpMethod.GET, "/admin/traveler/1/profile", "", customerRole, true),
            listOf(HttpMethod.GET, "/admin/traveler/1/tickets", "", customerRole, true),
            listOf(
                HttpMethod.POST,
                "/my/tickets",
                "{\"cmd\": \"buy_tickets\", \"quantity\": \"2\", \"zones\": \"ABC\"}",
                allRoles,
                false
            ),
            listOf(
                HttpMethod.PUT,
                "/my/profile",
                "{\"name\":\"test\", \"address\":\"test\", \"date_of_birth\":\"2022-05-18\", \"number\":\"0123456789\"}",
                allRoles,
                false
            )
        )
        for (e in endpoints) {
            sendAuthAndNotAuthRequests(
                e[0] as HttpMethod,
                e[1] as String,
                e[2] as String,
                e[3] as List<String>,
                e[4] as Boolean
            )
        }
    }

}