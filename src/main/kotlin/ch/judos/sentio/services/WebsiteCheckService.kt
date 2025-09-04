package ch.judos.sentio.services

import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.Website
import ch.judos.sentio.entities.WebsiteStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.logging.Log
import io.quarkus.narayana.jta.runtime.TransactionConfiguration
import io.quarkus.scheduler.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDateTime
import jakarta.persistence.PersistenceContext
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes

@ApplicationScoped
class WebsiteCheckService(
		private var sslCertificateService: SslCertificateService,
		val query: JPAQueryFactory,
		@PersistenceContext
		val entityManager: EntityManager
) {
	
	val qWebsite = QWebsite.website
	
	var lastUpdate: LocalDateTime = LocalDateTime.now()
	
	fun getNextUpdate(): LocalDateTime = lastUpdate.plusMinutes(5)
	
	@Scheduled(every = "5m")
	@Transactional
	fun validateWebsites() = runBlocking {
		lastUpdate = LocalDateTime.now()
		Log.info("Starte parallele Website-Validierung...")

		val websites = query.selectFrom(qWebsite).fetch()
		val jobs = websites.mapIndexed { i, site ->
			launch(Dispatchers.IO) {
				// Kleine Verzögerung, damit nicht alle Anfragen gleichzeitig starten
				sleep((i * 500).toLong())
				Log.info("validating ${site.name} (${site.url})...")
				checkWebsite(site)
			}
		}
		jobs.joinAll()
	}
	
	@Transactional
	fun checkWebsite(site: Website) {
		try {
					// SSL-Check (optional, nur für HTTPS)
					val sslExpiryDays = sslCertificateService.checkCertificateExpiry(site.url)
					// Erreichbarkeits-Check
					val reachable = checkWebsite(site.url)
					val newStatus = if (reachable) WebsiteStatus.OK else WebsiteStatus.FAILED
					updateWebsiteStatus(site.id!!, newStatus, reachable)
				} catch (ex: Exception) {
					updateWebsiteStatus(site.id!!, WebsiteStatus.FAILED, false, ex.message)
				}
	}
	
	fun updateWebsiteStatus(id: Int, newStatus: WebsiteStatus, reachable: Boolean,
			errorMsg: String? = null) {
		val site = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne() ?: return
		if (site.rStatus != newStatus) {
			site.rStatus = newStatus
			Log.info("Status von ${site.name} auf $newStatus gesetzt.")
		}
		if (newStatus == WebsiteStatus.OK && reachable) {
			site.rLastCheck = LocalDateTime.now()
		}
		if (newStatus == WebsiteStatus.FAILED && errorMsg != null) {
			Log.warn("Fehler bei Validierung von ${site.name}: $errorMsg")
		}
		entityManager.persist(site)
	}
	
	@Transactional
	fun refreshAllWebsitesInactive() {
		Log.info("Setze alle Websites beim Start auf INACTIVE...")
		for (site in query.selectFrom(qWebsite).fetch()) {
			site.rStatus = WebsiteStatus.INACTIVE
			entityManager.persist(site)
		}
	}

	fun onStartup(@Observes event: StartupEvent) {
		refreshAllWebsitesInactive()
	}
	
	private fun checkWebsite(url: String): Boolean {
		return try {
			val uri = URI(url)
			val connection = uri.toURL().openConnection() as HttpURLConnection
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
