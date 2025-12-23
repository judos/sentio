package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.Monitored
import ch.judos.sentio.model.MonitorField
import kotlinx.serialization.Serializable
import java.net.URI
import java.security.cert.X509Certificate
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class SslExpiryMonitor : Monitor<SslExpiryMonitor.Settings> {
	
	override val settingsSerializer = Settings.serializer()
	
	@Serializable
	class Settings {
		lateinit var url: String
	}
	
	override fun checkAndReturnError(config: Monitored): String? {
		try {
			val settings = getSettings(config)
			val uri = URI(settings.url)
			if (!uri.scheme.equals("https", ignoreCase = true))
				Triple(false, "Not a https url", 0L)
			val host = uri.host
			val port = if (uri.port != -1) uri.port else 443
			val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
			factory.createSocket(host, port).use { socket ->
				val sslSocket = socket as SSLSocket
				sslSocket.startHandshake()
				val session = sslSocket.session
				val certs = session.peerCertificates
				val x509 = certs[0] as X509Certificate
				val expiry = x509.notAfter.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				val today = LocalDate.now()
				val days = ChronoUnit.DAYS.between(today, expiry)
				return if (days <= 7) "SSL-Certificate expires in $days days"
				else null
			}
		} catch (e: Exception) {
			return "SSL-Check failed: ${e.message}"
		}
	}
	
	override fun getKey() = "ssl_expiry"
	
	override fun getDefaultAlertIfFailingForMin() = 0
	
	override fun getDefaultCheckEveryMin() = 24 * 60 // daily
	
	override fun getName() = "SSL Expiry"
	override fun getFields(): List<MonitorField> {
		return listOf(MonitorField("url", "URL of Website", "url"))
	}
}

