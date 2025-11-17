package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.WebsiteConfig
import java.net.HttpURLConnection
import java.net.URI

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
			e.message ?: "Unknown error"
		}
	}
	
	override fun getKey() = "reachability"
	
	override fun getDefaultAlertIfFailingForMin() = 30
	
	override fun getDefaultCheckEveryMin() = 5
	
	override fun getName() = "Reachability"
}
