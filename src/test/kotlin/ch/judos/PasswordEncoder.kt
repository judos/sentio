package ch.judos

import ch.judos.sentio.services.PasswordService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PasswordServiceTest {
	@Test
	fun testCreateAndVerify() {
		val password = "TestPassword123!"
		val hash = PasswordService.createSaltAndHash(password)
		assertTrue(PasswordService.verifyPw(password, hash),
			"Das Passwort sollte erfolgreich verifiziert werden.")
		assertFalse(PasswordService.verifyPw("FalschesPasswort", hash),
			"Ein falsches Passwort darf nicht verifiziert werden.")
	}
}

fun main() {
	print("Enter your password: ")
	val pw = readln()
	val hash = PasswordService.createSaltAndHash(pw)
	println("Generated hash: $hash")
}
