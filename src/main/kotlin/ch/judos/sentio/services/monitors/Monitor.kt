package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.Monitored
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface Monitor<T : Any> {
	
	/** run check for specific website */
	fun checkAndReturnError(config: Monitored): String?
	
	/** unique value for entries in db */
	fun getKey(): String
	fun getDefaultAlertIfFailingForMin(): Int
	fun getDefaultCheckEveryMin(): Int
	fun getName(): String
	
	val settingsSerializer: KSerializer<T>
	fun getSettings(monitored: Monitored): T {
		return Json.decodeFromString(settingsSerializer, monitored.settings)
	}
	
	fun getDefault(): Monitored {
		return Monitored().also {
			it.monitor = getKey()
			it.checkEveryMin = getDefaultCheckEveryMin()
			it.alertIfFailingForMin = getDefaultAlertIfFailingForMin()
		}
	}
	
	companion object {
		val monitors = listOf(
			SslExpiryMonitor(),
			ReachabilityMonitor()
		)
	}
}
