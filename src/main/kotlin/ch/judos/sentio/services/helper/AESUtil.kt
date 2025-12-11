package ch.judos.sentio.services.helper

import io.quarkus.logging.Log
import java.security.InvalidParameterException
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.system.measureTimeMillis

/**
 * @param aesIv 16 bytes encoded as base64
 */
class AESUtil(
	private val aesPassword: String,
	private val aesIv: String
) {
	
	private val random = RandomHelper(SecureRandom())
	private val cipherTransformation = "AES/CBC/PKCS5Padding"
	private val algorithm = "PBKDF2WithHmacSHA256"
	private val encoder = Base64.getEncoder()
	private val decoder = Base64.getDecoder()
	private val ivSpec = IvParameterSpec(decoder.decode(aesIv))
	
	init {
		if (ivSpec.iv.size != 16) {
			throw RuntimeException("Invalid iv, must be 16 bytes long")
		}
	}
	
	/**
	 * if salt is null it will be generated
	 */
	private fun createKeyAndSalt(saltOptional: ByteArray? = null): Pair<SecretKeySpec, ByteArray> {
		val secret: SecretKeySpec
		val salt = saltOptional ?: random.bytes(16)
		val ms = measureTimeMillis {
			val factory = SecretKeyFactory.getInstance(algorithm)
			val spec: KeySpec = PBEKeySpec(aesPassword.toCharArray(), salt, 16384, 256)
			secret = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
		}
		Log.debug("key derivation took $ms ms, this is normal and should take some time")
		return Pair(secret, salt)
	}
	
	@Throws(InvalidParameterException::class)
	fun encrypt(input: String): String {
		try {
			val (key, salt) = createKeyAndSalt()
			val cipher: Cipher = Cipher.getInstance(cipherTransformation)
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
			val encrypted: ByteArray = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
			return encoder.encodeToString(encrypted) + ":" + encoder.encodeToString(salt)
		} catch (e: Exception) {
			throw InvalidParameterException(e.message, e)
		}
	}
	
	@Throws(InvalidParameterException::class)
	fun decrypt(encrypted: String): String {
		try {
			val data = encrypted.split(":")
			val salt = decoder.decode(data[1])
			val cipherData = decoder.decode(data[0])
			val (key, _) = createKeyAndSalt(salt)
			val cipher = Cipher.getInstance(cipherTransformation)
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
			val plainText = cipher.doFinal(cipherData)
			return String(plainText, Charsets.UTF_8)
		} catch (e: Exception) {
			throw InvalidParameterException(e.message, e)
		}
	}
	
}
