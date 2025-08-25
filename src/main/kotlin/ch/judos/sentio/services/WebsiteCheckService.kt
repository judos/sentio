package ch.judos.sentio.services

import ch.judos.sentio.entities.Website
import ch.judos.sentio.entities.WebsiteStatus
// import com.querydsl.jpa.impl.JPAQueryFactory
// import ch.judos.sentio.entities.QWebsite
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDateTime

@ApplicationScoped
class WebsiteCheckService(
	@Inject
	private var sslCertificateService: SslCertificateService
) {
	
	var lastUpdate: LocalDateTime = LocalDateTime.now()
	
	fun getNextUpdate(): LocalDateTime = lastUpdate.plusMinutes(5)
	
	@PersistenceContext
	lateinit var em: EntityManager
	
	
	@Scheduled(every = "5m")
	fun validateWebsites() = runBlocking {
		lastUpdate = LocalDateTime.now()
		Log.info("Starte parallele Website-Validierung...")
		// val now = LocalDateTime.now()
		// val qWebsite = QWebsite.website
		// val queryFactory = JPAQueryFactory(em)
		// val websites = queryFactory.selectFrom(qWebsite)
		// 	.where(
		// 		qWebsite.rCheckEveryMin.isNotNull,
		// 		qWebsite.rLastCheck.isNull
		// 			.or(qWebsite.rLastCheck.addMinutes(qWebsite.rCheckEveryMin.castToNum().longValue()).before(now))
		// 	)
		// 	.fetch()
		val websites = Website.listAll()
		val jobs = websites.mapIndexed { i, site ->
			launch(Dispatchers.Default) {
				// Kleine Verzögerung, damit nicht alle Anfragen gleichzeitig starten
				sleep((i * 500).toLong())
				Log.info("validating ${site.name} (${site.url})...")
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
		}
		jobs.joinAll()
	}
	
	@Transactional
	fun updateWebsiteStatus(id: Long, newStatus: WebsiteStatus, reachable: Boolean,
			errorMsg: String? = null) {
		val site = Website.findById(id) ?: return
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
		site.persistAndFlush()
		Log.info("Status von ${site.name} ist ${site.rStatus}.")
	}
	
	@PostConstruct
	@Transactional
	fun refreshAllWebsitesInactive() {
		Log.info("Setze alle Websites beim Start auf INACTIVE...")
		for (site in Website.listAll()) {
			site.rStatus = WebsiteStatus.INACTIVE
			site.persist()
		}
		validateWebsites()
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
