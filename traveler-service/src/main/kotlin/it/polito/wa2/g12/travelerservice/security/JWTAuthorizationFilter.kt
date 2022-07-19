package it.polito.wa2.g12.travelerservice.security

import it.polito.wa2.g12.travelerservice.config.SecurityProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    private val securityProperties: SecurityProperties,
) : OncePerRequestFilter() {

    @Autowired
    lateinit var validationJwt: JWTUtils

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = req.getHeader(securityProperties.headerString)
        if (header == null || !header.startsWith(securityProperties.tokenPrefix)) {
            chain.doFilter(req, res)
            return
        }

        if (header.let { validationJwt.validateJwt(it) }) {
            val authorities = ArrayList<GrantedAuthority>()
            val a = header.let { validationJwt.getDetailsJwt(it) }
            for (i in a.roles) {
                authorities.add(SimpleGrantedAuthority(i.trim()))
            }
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(a.username, null, authorities)
        }

        chain.doFilter(req, res)
    }

}