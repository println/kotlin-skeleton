package skeleton.app.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import skeleton.app.domain.account.AccountRepository
import skeleton.app.support.jwt.JwtAuthenticationFilter
import kotlin.jvm.optionals.getOrNull


@Configuration
@EnableWebSecurity
class SecurityConfig(
        private val jwtAuthenticationFilter: JwtAuthenticationFilter,
        private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun formLoginFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it
                            .requestMatchers(
                                    AntPathRequestMatcher("/api/v1/auth/**"),
                                    AntPathRequestMatcher("/api/v1/user"),
                                    AntPathRequestMatcher("/api/v1/user/**"),
                                    AntPathRequestMatcher("/v3/api-docs"),
                                    AntPathRequestMatcher("/v3/api-docs/**"),
                                    AntPathRequestMatcher("/swagger-ui/**"),
                                    AntPathRequestMatcher("/swagger-ui.html")
                            ).permitAll()
                            .anyRequest().authenticated()
                }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .logout { logout ->
                    logout.logoutUrl("/logout")
                            .logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                            .invalidateHttpSession(true)
                }
                .build()
    }

}