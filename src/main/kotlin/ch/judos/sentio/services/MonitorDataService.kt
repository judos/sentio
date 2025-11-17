package ch.judos.sentio.services

import ch.judos.sentio.entities.MonitorData
import ch.judos.sentio.entities.MonitorError
import ch.judos.sentio.entities.QMonitorData
import ch.judos.sentio.entities.Website
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
	
	val qData: QMonitorData = QMonitorData.monitorData
	
	/** if error is null it is counted as success */
	fun addData(website: Website, monitorKey: String, error: String?) {
		val data = query.selectFrom(qData).where(
			qData.website.id.eq(website.id),
			qData.monitor.eq(monitorKey), qData.date.eq(LocalDate.now())
		).fetchOne()
			?: MonitorData().apply {
				this.website = website
				this.monitor = monitorKey
				this.date = LocalDate.now()
				this.firstCheck = LocalTime.now()
				this.succeeded = 0
				this.failed = 0
			}
		if (error == null) {
			data.succeeded += 1
		} else {
			data.failed += 1
			val err = MonitorError().apply {
				this.website = website
				this.monitor = monitorKey
				this.dateTime = LocalDateTime.now()
				this.message = error
			}
			entityManager.persist(err)
		}
		entityManager.persist(data)
	}
	
}
