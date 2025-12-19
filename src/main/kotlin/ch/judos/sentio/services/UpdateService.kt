package ch.judos.sentio.services


import ch.judos.sentio.entities.QData
import ch.judos.sentio.entities.QWebsiteConfig
import ch.judos.sentio.entities.WebsiteConfig
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
	var entityManager: EntityManager,
	val monitorDataService: MonitorDataService
) {
	
	val monitorMap = MonitorService.monitors.associateBy { it.getKey() }
	val qConfigs: QWebsiteConfig = QWebsiteConfig.websiteConfig
	val qData: QData = QData.data
	
	@Scheduled(every = "60s")
	@Transactional
	fun validateWebsites() = runBlocking {
		Log.debug("Checking monitors for all sites...")
		
		val configs: List<WebsiteConfig> = query.selectFrom(qConfigs).fetch()
		val jobs = mutableListOf<Job>()
		configs.forEachIndexed { i, config ->
			// fetch last monitor data
			val data = query.selectFrom(qData).where(
				qData.website.id.eq(config.website.id),
				qData.monitor.eq(config.monitor)
			).orderBy(qData.lastCheck.desc()).limit(1).fetchOne()
			var refresh = true
			if (data != null) {
				val diffSec = Duration.between(data.lastCheck, LocalDateTime.now()).toSeconds()
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
		val message = monitor.checkAndReturnError(config)
		monitorDataService.addData(website, config.monitor, message)
	}
	
	fun onStartup(@Observes event: StartupEvent) {
		// init anything
	}
	
	
}

