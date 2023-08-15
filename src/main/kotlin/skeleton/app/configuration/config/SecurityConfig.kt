package skeleton.app.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.core.auth.jwt.JwtAuthFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
        private val jwtAuthFilter: JwtAuthFilter,
        private val authenticationProvider: AuthenticationProvider,
        private val logoutHandler: LogoutHandler
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
                                    AntPathRequestMatcher("/api/v1/account"),
                                    AntPathRequestMatcher("/api/v1/account/**"),
                                    AntPathRequestMatcher("/v3/api-docs"),
                                    AntPathRequestMatcher("/v3/api-docs/**"),
                                    AntPathRequestMatcher("/swagger-ui/**"),
                                    AntPathRequestMatcher("/swagger-ui.html")
                            ).permitAll()
                            .anyRequest().authenticated()
                }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
                .logout { logout ->
                    logout.logoutUrl("${Endpoints.AUTH}/logout")
                            .addLogoutHandler(logoutHandler)
                            .logoutSuccessHandler(({ _, _, _ -> SecurityContextHolder.clearContext() }))
                            .invalidateHttpSession(true)
                }
                .build()
    }

}