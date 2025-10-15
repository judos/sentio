package ch.judos.sentio.services


import ch.judos.sentio.entities.QWebsiteConfig
import ch.judos.sentio.entities.QWebsiteMonitorData
import ch.judos.sentio.entities.WebsiteConfig
import ch.judos.sentio.entities.WebsiteMonitorData
import ch.judos.sentio.services.monitors.MonitorService
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToLong

@ApplicationScoped
class UpdateService(
		var query: JPAQueryFactory,
		@PersistenceContext
		var entityManager: EntityManager
) {
	
	val monitorMap = MonitorService.monitors.associateBy { it.getKey() }
	val qConfigs: QWebsiteConfig = QWebsiteConfig.websiteConfig
	val qData: QWebsiteMonitorData = QWebsiteMonitorData.websiteMonitorData
	
	@Scheduled(every = "1m")
	@Transactional
	fun validateWebsites() = runBlocking {
		Log.info("Starte parallele Website-Validierung...")
		
		val configs: List<WebsiteConfig> = query.selectFrom(qConfigs).fetch()
		val jobs = mutableListOf<Job>()
		configs.forEachIndexed { i, config ->
			// fetch last monitor data
			val data = query.selectFrom(qData).where(qData.website.id.eq(config.id),
				qData.monitor.eq(config.monitor)).orderBy(qData.datetime.desc()).limit(1).fetchOne()
			var refresh = true
			if (data != null) {
				val diffSec = Duration.between(data.datetime, LocalDateTime.now()).toSeconds()
				val diffMin = (diffSec / 60.0).roundToLong()
				if (diffMin < config.checkEveryMin) {
					refresh = false
				}
			}
			if (refresh) {
				jobs.add(launch(Dispatchers.IO) {
					// Kleine Verzögerung, damit nicht alle Anfragen gleichzeitig starten
					sleep((i * 500).toLong())
					checkWebsite(config)
				})
			}
		}
		jobs.joinAll()
	}
	
	@Transactional
	fun checkWebsite(config: WebsiteConfig) {
		val website = config.website
		val monitor = monitorMap[config.monitor]!!
		val (success, message, value) = monitor.check(config)
		val data = WebsiteMonitorData().also {
			it.website = website
			it.monitor = config.monitor
			it.datetime = LocalDateTime.now()
			it.success = success
			it.message = message
			it.value = value
		}
		entityManager.persist(data)
	}
	
	fun onStartup(@Observes event: StartupEvent) {
		// init anything
		// for (site in query.selectFrom(qWebsite).fetch()) {
		// 	site.rStatus = WebsiteStatus.INACTIVE
		// 	entityManager.persist(site)
		// }
	}
	
	
}

