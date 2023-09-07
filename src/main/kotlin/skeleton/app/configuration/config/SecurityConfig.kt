package skeleton.app.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import skeleton.app.configuration.constants.Endpoints.ACCOUNT_ACTIVATION
import skeleton.app.configuration.constants.Endpoints.AUTH
import skeleton.app.configuration.constants.Endpoints.MANAGEMENT
import skeleton.app.support.access.account.AccountRole.*
import skeleton.app.support.access.account.Permission.*
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
                .cors { it.disable() }
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it
                            .requestMatchers(
                                    AntPathRequestMatcher("$AUTH/**"),
                                    AntPathRequestMatcher(ACCOUNT_ACTIVATION),
                                    AntPathRequestMatcher("/health"),
                                    AntPathRequestMatcher("/v3/api-docs"),
                                    AntPathRequestMatcher("/v3/api-docs.yaml"),
                                    AntPathRequestMatcher("/v3/api-docs/**"),
                                    AntPathRequestMatcher("/swagger-ui/**"),
                                    AntPathRequestMatcher("/swagger-ui.html"),
                                    AntPathRequestMatcher("/h2-console"),
                                    AntPathRequestMatcher("/h2-console/**")
                            ).permitAll()
                            .requestMatchers(AntPathRequestMatcher("$MANAGEMENT/**")).hasAnyRole(ADMIN.name, MANAGER.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(GET, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_READ.name, MANAGER_READ.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(POST, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_CREATE.name, MANAGER_CREATE.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(PUT, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_UPDATE.name, MANAGER_UPDATE.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(DELETE, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_DELETE.name, MANAGER_DELETE.name)
                            .anyRequest().authenticated()
                }
                .headers {
                    it.frameOptions { t ->
                        t.sameOrigin()
                    }
                }
                .sessionManagement { it.sessionCreationPolicy(STATELESS) }
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
                .logout { logout ->
                    logout
                            .addLogoutHandler(logoutHandler)
                            .logoutRequestMatcher(AntPathRequestMatcher("$AUTH/logout", "POST"))
                            .logoutSuccessHandler(({ _, _, _ -> SecurityContextHolder.clearContext() }))
                            .invalidateHttpSession(true)
                }
                .build()
    }

}