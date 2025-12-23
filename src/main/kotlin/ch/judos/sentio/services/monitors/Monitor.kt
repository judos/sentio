package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.Monitored
import ch.judos.sentio.model.MonitorField
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

interface Monitor<T : Any> {
	
	/** run check for specific website */
	fun checkAndReturnError(config: Monitored): String?
	
	/** unique value for entries in db */
	fun getKey(): String
	fun getDefaultAlertIfFailingForMin(): Int
	fun getDefaultCheckEveryMin(): Int
	fun getName(): String
	fun getFields(): List<MonitorField>
	
	val settingsSerializer: KSerializer<T>
	
	fun getSettings(monitored: Monitored): T {
		val jsonString = Json.encodeToString(
			MapSerializer(String.serializer(), String.serializer()),
			monitored.settings
		)
		return Json.decodeFromString(settingsSerializer, jsonString)
	}
	
	fun getDefault(): Monitored {
		return Monitored().also {
			it.monitor = getKey()
			it.name = getName()
			it.checkEveryMin = getDefaultCheckEveryMin()
			it.alertIfFailingForMin = getDefaultAlertIfFailingForMin()
			it.settings = mapOf()
		}
	}
	
	companion object {
		val monitors = listOf(
			SslExpiryMonitor(),
			ReachabilityMonitor()
		)
		
		fun byKey(key: String): Monitor<*>? {
			return monitors.find { it.getKey() == key }
		}
	}
}
