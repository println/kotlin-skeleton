package skeleton.app.configuration.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import skeleton.app.configuration.properties.JwtProperties

@Configuration
@EnableConfigurationProperties(JwtProperties ::class)
class JwtConfig {
}