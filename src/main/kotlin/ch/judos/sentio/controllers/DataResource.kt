package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QData
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QMonitored
import ch.judos.sentio.model.DataPeriod
import ch.judos.sentio.services.ImageService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response


@Path("/api/data")
@Produces(MediaType.APPLICATION_JSON)
class DataResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager,
		var imageService: ImageService,
) {
	
	val qData = QData.data
	val qMonitored = QMonitored.monitored
	val qError = QMonitorError.monitorError
	
	@GET
	@Path("/{monitoredId}")
	fun generateImage(
			monitoredId: Long,
			@CookieParam("sentio_dateRange") daysStr: String?,
	): Response {
		query.selectFrom(qMonitored).where(qMonitored.id.eq(monitoredId)).fetchOne()
			?: return Response.status(Response.Status.NOT_FOUND).build()
		val days = daysStr?.toIntOrNull() ?: 7
		val period = DataPeriod(days)
		
		query.selectFrom(qData).where(
			qData.monitored.id.eq(monitoredId),
			qData.date.goe(period.startTime.toLocalDate()),
		).fetch().forEach { period.addData(it) }
		
		val image = period.toImage(600, 50)
		return imageService.image2Response(image)
	}
	
}
