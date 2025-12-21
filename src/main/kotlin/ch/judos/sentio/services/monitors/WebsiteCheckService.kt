package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.WebsiteConfig
import io.quarkus.logging.Log
import java.net.HttpURLConnection
import java.net.URI

const val REACHABILITY_MONITOR_KEY = "reachability"
class WebsiteCheckService : MonitorService {
	
	override fun checkAndReturnError(config: WebsiteConfig): String? {
		return try {
			val uri = URI(config.website.url)
			val connection = uri.toURL().openConnection() as HttpURLConnection
			connection.connectTimeout = 30_000
			connection.readTimeout = 30_000
			connection.requestMethod = "HEAD"
			connection.connect()
			val code = connection.responseCode
			val success = code in 200..399
			if (success) null else "HTTP $code"
		} catch (e: Exception) {
			Log.warn("Website Check Failed", e)
			val c = e::class.simpleName!!.removeSuffix("Exception")
			val msg = e.message ?: "Unknown error"
			"$c: $msg"
		}
	}
	
	override fun getKey() = REACHABILITY_MONITOR_KEY
	
	override fun getDefaultAlertIfFailingForMin() = 30
	
	override fun getDefaultCheckEveryMin() = 5
	
	override fun getName() = "Reachability"
}
