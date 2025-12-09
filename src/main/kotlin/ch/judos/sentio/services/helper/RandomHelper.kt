package ch.judos.sentio.services.helper

import java.util.*

class RandomHelper(
	private val random: Random
) {
	
	fun hexString(length: Int): String {
		return str(length, HEX)
	}
	
	fun str(length: Int, characters: String): String {
		val stringBuilder = StringBuilder()
		for (i in 1..length) {
			stringBuilder.append(characters[random.nextInt(characters.length)])
		}
		return stringBuilder.toString()
	}
	
	fun bytes(length: Int): ByteArray {
		val r = ByteArray(length)
		random.nextBytes(r)
		return r
	}
	
	companion object {
		const val LETTERS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		const val LETTERS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
		const val LETTERS = LETTERS_LOWERCASE + LETTERS_UPPERCASE
		const val DIGITS = "0123456789"
		const val HEX = DIGITS + "abcdef"
		const val ALPHA_NUM = LETTERS + DIGITS
	}
	
}
