package it.polito.wa2.g12.travelerservice.config

import it.polito.wa2.g12.travelerservice.security.JWTAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig(
    val securityProperties: SecurityProperties,
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions
            .and()
            .authorizeRequests()
            .antMatchers("/admin/**").permitAll()
            .antMatchers("/my/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(authorizationFilterBean(), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun authorizationFilterBean(): JWTAuthorizationFilter {
        return JWTAuthorizationFilter(securityProperties)
    }

}