package ch.judos.sentio.services.helper

interface Encryption {
	
	fun encrypt(input: String): String
	fun decrypt(encrypted: String): String
	
}
