package ch.judos.sentio.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Base64

class JwtServiceTest {

    @Test
    fun `create and parse token`() {
        // create a random 32-byte key and base64 encode
        val keyBytes = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
        val secretB64 = Base64.getEncoder().encodeToString(keyBytes)

        val jwtService = JwtService(secretB64, 3600)

        val token = jwtService.createToken(12345L)
        assertNotNull(token)
        val subject = jwtService.getSubject(token)
        assertEquals("12345", subject)
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

