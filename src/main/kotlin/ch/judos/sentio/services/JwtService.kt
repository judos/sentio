package ch.judos.sentio.services

import ch.judos.sentio.entities.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@ApplicationScoped
class JwtService(
	@ConfigProperty(name = "sentio.jwt.secret")
	private var jwtSecret: String,
	@ConfigProperty(name = "sentio.jwt.expiration")
	private var jwtExpirationSeconds: Long
) {
	
	private val signingKey: SecretKey by lazy {
		// JWT expects a binary key; allow base64 encoded secret or plain string
		val keyBytes = Base64.getDecoder().decode(jwtSecret)
		Keys.hmacShaKeyFor(keyBytes)
	}
	
	fun createToken(user: User): String {
		val now = Date.from(Instant.now())
		val exp = Date.from(Instant.now().plusSeconds(jwtExpirationSeconds))
		return Jwts.builder()
			.subject(user.username)
			.issuedAt(now)
			.expiration(exp)
			.claim("userId", user.id)
			.signWith(signingKey, Jwts.SIG.HS256)
			.compact()
	}
	
	@Throws(JwtException::class)
	fun parseToken(token: String): Jws<Claims> {
		return Jwts.parser()
			.verifyWith(signingKey)
			.build()
			.parseSignedClaims(token)
	}
	
	fun getSubject(token: String): String? {
		return try {
			parseToken(token).payload.subject
		} catch (_: JwtException) {
			null
		}
	}
}
