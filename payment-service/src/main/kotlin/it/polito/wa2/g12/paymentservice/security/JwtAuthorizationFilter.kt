package it.polito.wa2.g12.paymentservice.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class JwtAuthorizationFilter(private val jwtParser: JwtUtils) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Extract header
        val token = exchange.request.headers.getFirst("Authorization")?.trim()?.split(" ")?.get(1)

        // Validate JWT
        if (token != null && jwtParser.validateJwt(token)) {
            val user = jwtParser.getDetailsJwt(token)
            val authenticatedUser =
                UsernamePasswordAuthenticationToken(user.username, null, user.roles.map { SimpleGrantedAuthority(it) })

            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticatedUser))
        }

        return chain.filter(exchange)
    }
}