package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QMonitorData
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.model.DataPeriod
import ch.judos.sentio.services.ImageService
import ch.judos.sentio.services.monitors.MonitorService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response


@Path("/api/monitor-data")
@Produces(MediaType.APPLICATION_JSON)
class DataResource(
	val query: JPAQueryFactory,
	val entityManager: EntityManager,
	var imageService: ImageService,
) {
	
	val qData = QMonitorData.monitorData
	val qError = QMonitorError.monitorError
	
	@GET
	@Path("/{id}/{monitorKey}")
	fun generateImage(
		id: Long,
		monitorKey: String,
		@CookieParam("sentio_dateRange") daysStr: String?,
	): Response {
		MonitorService.monitors.find { it.getKey() == monitorKey }
			?: return Response.status(Response.Status.NOT_FOUND).build()
		val days = daysStr?.toIntOrNull() ?: 7
		val period = DataPeriod(days)
		
		query.selectFrom(qData).where(
			qData.website.id.eq(id),
			qData.monitor.eq(monitorKey),
			qData.date.goe(period.startTime.toLocalDate()),
		).fetch().forEach { period.addData(it) }
		// val errors: List<MonitorError> = query.selectFrom(qError).where(
		// 	qError.website.id.eq(id),
		// 	qError.monitor.eq(monitorKey),
		// 	qError.dateTime.goe(period.startTime)
		// ).orderBy().fetch()
		val image = period.toImage(600, 50)
		return imageService.image2Response(image)
	}
	
}
