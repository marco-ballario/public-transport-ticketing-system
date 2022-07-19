package it.polito.wa2.g12.travelerservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.g12.travelerservice.config.SecurityProperties
import it.polito.wa2.g12.travelerservice.dto.UserDetailsDTO
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Configuration
class JWTUtils(private val securityProperties: SecurityProperties) {

    private val claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(securityProperties.secret)).build()

    fun validateJwt(token: String): Boolean {
        return try {
            claims.parseClaimsJws(token.replace(securityProperties.tokenPrefix, ""))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDetailsJwt(jwt: String): UserDetailsDTO {
        val decodedJwt = claims.parseClaimsJws(jwt.replace(securityProperties.tokenPrefix, ""))
        val authorities = ArrayList<GrantedAuthority>()
        val userDetails = decodedJwt.body["roles"].toString().replace("[", "").replace("]", "")

        for (i in userDetails.split(",")) {
            authorities.add(SimpleGrantedAuthority(i.trim()))
        }

        return UserDetailsDTO(decodedJwt.body.subject, userDetails.split(","))
    }

}