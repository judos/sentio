package ch.judos.sentio.services

import ch.judos.sentio.services.helper.RandomHelper
import java.security.SecureRandom
import java.util.Base64

class RandomTests {
}

fun main() {
	val rnd = RandomHelper(SecureRandom())
	println("random pw: " + rnd.str(16, RandomHelper.ALPHA_NUM))
	
	val iv = rnd.bytes(16)
	val ivB64 = Base64.getEncoder().encodeToString(iv)
	println("random iv: $ivB64")
}
