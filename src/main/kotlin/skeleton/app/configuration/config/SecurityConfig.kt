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
import skeleton.app.configuration.constants.Endpoints.ACCOUNT
import skeleton.app.configuration.constants.Endpoints.AUTH
import skeleton.app.configuration.constants.Endpoints.MANAGEMENT
import skeleton.app.configuration.constants.Endpoints.USER
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
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it
                            .requestMatchers(
                                    AntPathRequestMatcher("$AUTH/**"),
                                    AntPathRequestMatcher(USER),
                                    AntPathRequestMatcher("$USER/**"),
                                    AntPathRequestMatcher(ACCOUNT),
                                    AntPathRequestMatcher("$ACCOUNT/**"),
                                    AntPathRequestMatcher("/v3/api-docs"),
                                    AntPathRequestMatcher("/v3/api-docs/**"),
                                    AntPathRequestMatcher("/swagger-ui/**"),
                                    AntPathRequestMatcher("/swagger-ui.html")
                            ).permitAll()
                            .requestMatchers(AntPathRequestMatcher("$MANAGEMENT/**")).hasAnyRole(ADMIN.name, MANAGER.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(GET, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_READ.name, MANAGER_READ.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(POST, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_CREATE.name, MANAGER_CREATE.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(PUT, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_UPDATE.name, MANAGER_UPDATE.name)
                            .requestMatchers(AntPathRequestMatcher.antMatcher(DELETE, "$MANAGEMENT/**")).hasAnyAuthority(ADMIN_DELETE.name, MANAGER_DELETE.name)
                            .anyRequest().authenticated()
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