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
import skeleton.app.support.access.auth.basic.jwt.JwtAuthFilter


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
                                    AntPathRequestMatcher("/${Endpoints.AUTH}/**"),
                                    AntPathRequestMatcher("/${Endpoints.USER}"),
                                    AntPathRequestMatcher("/${Endpoints.USER}/**"),
                                    AntPathRequestMatcher("/${Endpoints.ACCOUNT}"),
                                    AntPathRequestMatcher("/${Endpoints.ACCOUNT}/**"),
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
                    logout
                            .addLogoutHandler(logoutHandler)
                            .logoutRequestMatcher(AntPathRequestMatcher("/${Endpoints.AUTH}/logout", "POST"))
                            .logoutSuccessHandler(({ _, _, _ -> SecurityContextHolder.clearContext() }))
                            .invalidateHttpSession(true)
                }
                .build()
    }

}