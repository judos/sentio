package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.WebsiteConfig
import java.net.URI
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSocketFactory
import java.time.ZoneId
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.net.ssl.SSLSocket

class SslCertificateService : MonitorService {

	override fun check(config: WebsiteConfig): Triple<Boolean, String?, Long> {
		return try {
			val website = config.website
			val uri = URI(website.url)
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
				if (days < 14) {
					Triple(false, "SSL-Certificate expires in $days days", days)
				}
				Triple(true, null, days)
			}
		} catch (e: Exception) {
			Triple(false, "SSL-Check failed: ${e.message}", 0L)
		}
	}
	
	override fun getKey() = "ssl_expiry"
	
	override fun getDefaultAlertIfFailingForMin() = 0
	
	override fun getDefaultCheckEveryMin() = 24 * 60 // daily
	
	override fun getName() = "SSL Expiry"
}

