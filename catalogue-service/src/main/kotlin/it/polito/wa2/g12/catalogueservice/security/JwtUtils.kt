package it.polito.wa2.g12.catalogueservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import it.polito.wa2.g12.catalogueservice.dto.UserDetailsDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class JwtUtils(@Value("\${jwt.secret-key}") private val key: String) {

    private val parser: JwtParser = Jwts
        .parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(key)))
        .build()

    fun validateJwt(authToken: String): Boolean {
        try {
            val body = parser.parseClaimsJws(authToken).body
            val roles = listOf("CUSTOMER", "ADMIN")
            val userId = body.getValue("sub").toString()
            val role = body["roles"]
                .toString()
                .replace("[", "")
                .replace("]", "")
                .split(",")

            if (userId.isBlank() || role.isEmpty() ||
                !(role.any { bItem -> roles.any { it.contains(bItem) } })
            )
                return false
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getDetailsJwt(jwt: String): UserDetailsDTO {
        val decodedJwt = parser.parseClaimsJws(jwt.replace("Bearer", ""))
        val authorities = ArrayList<GrantedAuthority>()
        val userDetails = decodedJwt.body["roles"].toString().replace("[", "").replace("]", "")

        for (i in userDetails.split(",")) {
            authorities.add(SimpleGrantedAuthority(i.trim()))
        }

        return UserDetailsDTO(decodedJwt.body.subject, userDetails.split(","))
    }
}