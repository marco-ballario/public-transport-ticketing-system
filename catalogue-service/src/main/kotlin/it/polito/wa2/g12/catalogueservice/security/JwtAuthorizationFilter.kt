package it.polito.wa2.g12.catalogueservice.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtAuthorizationFilter(private val jwtParser: JwtUtils) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Gets the JWT from the HTTP header
        val jwt = exchange.request.headers
            .getFirst("Authorization")?.trim()?.split(" ")?.get(1)

        // Validates the extracted JWT
        if (jwt != null && jwtParser.validateJwt(jwt)) {
            val user = jwtParser.getDetailsJwt(jwt)
            val authenticatedUser = UsernamePasswordAuthenticationToken(
                user.username,
                null,
                user.roles.map { SimpleGrantedAuthority(it) }
            )
            return chain.filter(exchange)
                .contextWrite(
                    ReactiveSecurityContextHolder.withAuthentication(authenticatedUser)
                )
        }

        return chain.filter(exchange)
    }
}