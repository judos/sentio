package ch.judos.sentio.services

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.net.URI
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSocketFactory
import java.time.ZoneId
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@ApplicationScoped
class SslCertificateService {
    /**
     * Prüft das Ablaufdatum des SSL-Zertifikats einer HTTPS-URL.
     * Gibt die verbleibenden Tage bis zum Ablauf zurück, oder null falls kein HTTPS oder Fehler.
     */
    fun checkCertificateExpiry(url: String): Long? {
        return try {
            val uri = URI(url)
            if (!uri.scheme.equals("https", ignoreCase = true)) return null
            val host = uri.host
            val port = if (uri.port != -1) uri.port else 443
            val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
            factory.createSocket(host, port).use { socket ->
                val sslSocket = socket as javax.net.ssl.SSLSocket
                sslSocket.startHandshake()
                val session = sslSocket.session
                val certs = session.peerCertificates
                val x509 = certs[0] as X509Certificate
                val expiry = x509.notAfter.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val today = LocalDate.now()
                val days = ChronoUnit.DAYS.between(today, expiry)
                if (days < 30) {
                    Log.warn("SSL-Zertifikat für $host läuft in $days Tagen ab! Ablaufdatum: $expiry")
                }
                days
            }
        } catch (e: Exception) {
            Log.warn("SSL-Check fehlgeschlagen für $url: ${e.message}")
            null
        }
    }
}

