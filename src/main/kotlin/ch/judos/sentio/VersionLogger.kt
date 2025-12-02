package ch.judos.sentio

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger

@ApplicationScoped
class VersionLogger {
	private val log: Logger = Logger.getLogger(this::class.java)
	
	fun onStartup(@Observes event: StartupEvent) {
		val javaVersion = System.getProperty("java.version")
		val kotlinVersion = KotlinVersion.CURRENT
		log.info("Kotlin: $kotlinVersion Java: $javaVersion")
	}
}
