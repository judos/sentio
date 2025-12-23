package ch.judos.sentio.services

import ch.judos.sentio.entities.Data
import ch.judos.sentio.entities.MonitorError
import ch.judos.sentio.entities.Monitored
import ch.judos.sentio.entities.QData
import ch.judos.sentio.extensions.update
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.floor

@ApplicationScoped
class MonitorDataService(
		var query: JPAQueryFactory,
		@PersistenceContext
		var entityManager: EntityManager
) {
	
	val qData: QData = QData.data
	
	/** if error is null it is counted as success */
	fun addData(monitored: Monitored, error: String?) {
		val data = query.selectFrom(qData).where(
			qData.monitored.eq(monitored),
			qData.date.eq(LocalDate.now())
		).fetchOne()
			?: Data().apply {
				this.monitored = monitored
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
				this.monitored = monitored
				this.dateTime = LocalDateTime.now()
				this.message = error
			}
			entityManager.persist(err)
		}
		data.lastCheck = LocalDateTime.now()
		entityManager.persist(data)
	}
	
	fun getUptimePercentage(monitored: Monitored?, days: Int): Map<Long, Int> {
		val dataList = query.selectFrom(qData).where(
			monitored?.let { qData.monitored.eq(monitored) },
			qData.date.goe(LocalDate.now().minusDays(days.toLong()))
		).fetch()
		val totalChecks = mutableMapOf<Long, Int>()
		val totalSucceeded = mutableMapOf<Long, Int>()
		for (data in dataList) {
			val monitoredID = data.monitored.id!!
			totalChecks.update(monitoredID, 0) { it + data.succeeded + data.failed }
			totalSucceeded.update(monitoredID, 0) { it + data.succeeded }
		}
		return totalChecks.mapValues { (websiteId, checks) ->
			val succeeded = totalSucceeded[websiteId] ?: 0
			if (checks == 0) 100 else floor(succeeded.toDouble() / checks * 100.0).toInt()
		}
	}
	
}
