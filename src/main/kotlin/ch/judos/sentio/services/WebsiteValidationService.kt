package ch.judos.sentio.services

import ch.judos.sentio.entities.Website
import ch.judos.sentio.entities.WebsiteStatus
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

@ApplicationScoped
class WebsiteValidationService {
	
	var lastUpdate: LocalDateTime = LocalDateTime.now()
	
	fun getNextUpdate(): LocalDateTime = lastUpdate.plusMinutes(5)
	
	@Scheduled(every = "5m")
	@Transactional
	fun validateWebsites() {
		lastUpdate = LocalDateTime.now()
		Log.info("Starte Website-Validierung...")
		for (site in Website.listAll()) {
			Log.info("validating ${site.name} (${site.url})...")
			try {
				val reachable = checkWebsite(site.url)
				val newStatus = if (reachable) WebsiteStatus.OK else WebsiteStatus.FAILED
				if (site.status != newStatus) {
					site.status = newStatus
					Log.info("Status von ${site.name} auf $newStatus gesetzt.")
				}
				if (newStatus == WebsiteStatus.OK) {
					site.lastSuccess = LocalDateTime.now()
				}
			} catch (ex: Exception) {
				site.status = WebsiteStatus.FAILED
				Log.warn("Fehler bei Validierung von ${site.name}: ${ex.message}")
			}
			site.persistAndFlush()
			Log.info("Status von ${site.name} ist ${site.status}.")
		}
		Website.flush()
	}
	
	@PostConstruct
	@Transactional
	fun refreshAllWebsitesInactive() {
		Log.info("Setze alle Websites beim Start auf INACTIVE...")
		for (site in Website.listAll()) {
			site.status = WebsiteStatus.INACTIVE
			site.persist()
		}
		validateWebsites()
	}
	
	private fun checkWebsite(url: String): Boolean {
		return try {
			val connection = URL(url).openConnection() as HttpURLConnection
			connection.connectTimeout = 30_000
			connection.readTimeout = 30_000
			connection.requestMethod = "HEAD"
			connection.connect()
			val code = connection.responseCode
			code in 200..399
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}
}
