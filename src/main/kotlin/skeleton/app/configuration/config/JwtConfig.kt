package skeleton.app.configuration.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import skeleton.app.support.jwt.JwtAuthenticationFilter
import skeleton.app.support.jwt.JwtService

@Configuration
class JwtConfig {

    @Bean
    fun jwtService(
            @Value("\${custom.jwt.expiration}") expirationTime: Number,
            @Value("\${custom.jwt.secret}") secretKey: String
    ) = JwtService(secretKey, expirationTime)

    @Bean
    fun jwtFilter(
            jwtService: JwtService,
            userDetailsService: UserDetailsService
    ) = JwtAuthenticationFilter(jwtService, userDetailsService)
}