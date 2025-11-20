package ch.judos.sentio.services

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordService {
	companion object {
		private const val ITERATIONS = 65536
		private const val KEY_LENGTH = 256
		private const val ALGORITHM = "PBKDF2WithHmacSHA256"
		private const val SEPARATOR = ":"
		
		fun createSaltAndHash(pw: String): String {
			val salt = ByteArray(16)
			SecureRandom().nextBytes(salt)
			val hash = encode(pw, salt)
			val saltString = Base64.getEncoder().encodeToString(salt)
			return "$saltString$SEPARATOR$hash"
		}
		
		fun verifyPw(pw: String, hash: String): Boolean {
			val parts = hash.split(SEPARATOR)
			if (parts.size != 2) return false
			val salt = Base64.getDecoder().decode(parts[0])
			val expectedHash = parts[1]
			return encode(pw, salt) == expectedHash
		}
		
		private fun encode(password: String, salt: ByteArray): String {
			val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
			val factory = SecretKeyFactory.getInstance(ALGORITHM)
			val hash = factory.generateSecret(spec).encoded
			return Base64.getEncoder().encodeToString(hash)
		}
	}
}

