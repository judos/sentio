package ch.judos.sentio.services

import ch.judos.sentio.entities.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class JwtServiceTest {
	
	@Test
	fun `create and parse token`() {
		// create a random 32-byte key and base64 encode
		val keyBytes = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
		val secretB64 = Base64.getEncoder().encodeToString(keyBytes)
		
		val jwtService = JwtService(secretB64, 3600)
		
		val user = User().apply {
			id = 12345L
			username = "testuser"
			password = "hashedpassword"
		}
		val token = jwtService.createToken(user)
		assertNotNull(token)
		val subject = jwtService.getSubject(token)
		assertEquals(user.username, subject)
	}
	
	@Test
	fun `invalid token returns null subject`() {
		val keyBytes = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
		val secretB64 = Base64.getEncoder().encodeToString(keyBytes)
		val jwtService = JwtService(secretB64, 3600)
		
		val invalidToken = "abc.def.ghi"
		val subject = jwtService.getSubject(invalidToken)
		assertNull(subject)
	}
}

