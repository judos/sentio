package ch.judos.sentio.services

import ch.judos.sentio.services.helper.AESUtil
import ch.judos.sentio.services.helper.Encryption
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
open class EncryptionService private constructor(
	@ConfigProperty(name = "sentio.aes.password")
	protected var aesPassword: String,
	@ConfigProperty(name = "sentio.aes.iv")
	protected var aesIv: String,
) : Encryption by AESUtil(aesPassword, aesIv) {

}
