package ch.judos.sentio.config


import ch.judos.sentio.config.auth.SentioIdentity
import io.quarkus.qute.TemplateGlobal
import jakarta.enterprise.inject.spi.CDI
import org.jboss.logging.Logger

@TemplateGlobal
object GlobalTemplateVars {
	
	private val log: Logger = Logger.getLogger(GlobalTemplateVars::class.java)
	
	@JvmStatic
	fun userId(): Long? = CDI.current().select(SentioIdentity::class.java).get().userId
	
	@JvmStatic
	fun username(): String? = CDI.current().select(SentioIdentity::class.java).get().username
}
