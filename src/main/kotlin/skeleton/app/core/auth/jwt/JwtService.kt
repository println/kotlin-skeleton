package skeleton.app.core.auth.jwt


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import skeleton.app.configuration.properties.JwtProperties
import java.security.Key
import java.util.*

@Service
class JwtService(
        private val jwtProperties: JwtProperties
) {

    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)

    fun generateToken(userDetails: UserDetails): String = generateToken(HashMap(), userDetails, jwtProperties.expiration)

    fun generateRefreshToken(userDetails: UserDetails): String = generateToken(HashMap(), userDetails, jwtProperties.refreshExpiration)

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails, expirationTime: Number): String = Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationTime.toLong()))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun extractAllClaims(token: String): Claims = Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token) //json web signature
            .body

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)

    private fun getSignInKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}