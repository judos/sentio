package ch.judos.sentio.services

import ch.judos.sentio.entities.Data
import ch.judos.sentio.entities.MonitorError
import ch.judos.sentio.entities.QData
import ch.judos.sentio.entities.Website
import ch.judos.sentio.extensions.update
import ch.judos.sentio.services.monitors.REACHABILITY_MONITOR_KEY
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ApplicationScoped
class MonitorDataService(
		var query: JPAQueryFactory,
		@PersistenceContext
		var entityManager: EntityManager
) {
	
	val qData: QData = QData.data
	
	/** if error is null it is counted as success */
	fun addData(website: Website, monitorKey: String, error: String?) {
		val data = query.selectFrom(qData).where(
			qData.website.id.eq(website.id),
			qData.monitor.eq(monitorKey), qData.date.eq(LocalDate.now())
		).fetchOne()
			?: Data().apply {
				this.website = website
				this.monitor = monitorKey
				this.date = LocalDate.now()
				this.firstCheck = LocalTime.now()
				this.succeeded = 0
				this.failed = 0
			}
		if (error == null) {
			data.succeeded += 1
		}
		else {
			data.failed += 1
			val err = MonitorError().apply {
				this.website = website
				this.monitor = monitorKey
				this.dateTime = LocalDateTime.now()
				this.message = error
			}
			entityManager.persist(err)
		}
		data.lastCheck = LocalDateTime.now()
		entityManager.persist(data)
	}
	
	fun getUptimePercentage(website: Website?, days: Int): Map<Long, Double> {
		val dataList = query.selectFrom(qData).where(
			website?.let { qData.website.id.eq(website.id) },
			qData.monitor.eq(REACHABILITY_MONITOR_KEY),
			qData.date.goe(LocalDate.now().minusDays(days.toLong()))
		).fetch()
		val totalChecks = mutableMapOf<Long, Int>()
		val totalSucceeded = mutableMapOf<Long, Int>()
		for (data in dataList) {
			val websiteId = data.website.id!!
			totalChecks.update(websiteId, 0) { it + data.succeeded + data.failed }
			totalSucceeded.update(websiteId, 0) { it + data.succeeded }
		}
		return totalChecks.mapValues { (websiteId, checks) ->
			val succeeded = totalSucceeded[websiteId] ?: 0
			if (checks == 0) 100.0 else (succeeded.toDouble() / checks) * 100.0
		}
	}
	
}
