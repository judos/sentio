package ch.judos.sentio.config


import ch.judos.sentio.config.auth.SentioIdentity
import io.quarkus.qute.TemplateGlobal
import jakarta.enterprise.inject.spi.CDI

@TemplateGlobal
object GlobalTemplateVars {
	
	@JvmStatic
	fun userId(): Long? = CDI.current().select(SentioIdentity::class.java).get().userId
	
	@JvmStatic
	fun username(): String? = CDI.current().select(SentioIdentity::class.java).get().username
}
