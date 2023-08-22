package skeleton.app.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("custom.jwt")
data class JwtProperties (
        var secret: String = "",
        var expiration: Number = 0,
        var refreshExpiration: Number = 0
)