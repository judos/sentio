package ch.judos.sentio.controllers

import ch.judos.sentio.entities.MonitorData
import ch.judos.sentio.entities.MonitorError
import ch.judos.sentio.entities.QMonitorData
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.services.ImageService
import ch.judos.sentio.services.monitors.MonitorService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import java.awt.Color
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt


@Path("/api/monitor-data")
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
			@PathParam("id") id: Long,
			@PathParam("monitorKey") monitorKey: String,
			@QueryParam("days") days: Int = 1
	): Response {
		val data: List<MonitorData> = query.selectFrom(qData).where(
			qData.website.id.eq(id),
			qData.monitor.eq(monitorKey),
			qData.date.goe(LocalDate.now().minusDays((days - 1).toLong())),
		).fetch()
		val errors: List<MonitorError> = query.selectFrom(qError).where(
			qError.website.id.eq(id),
			qError.monitor.eq(monitorKey),
			qError.dateTime.goe(LocalDate.now().minusDays((days - 1).toLong()).atStartOfDay())
		).fetch()
		MonitorService.monitors.find { it.getKey() == monitorKey }
			?: return Response.status(Response.Status.NOT_FOUND).build()
		val dataPoints = data.sumOf { it.succeeded + it.failed }
		val firstCheck = data.minOfOrNull { it.date.atTime(it.firstCheck) } ?: LocalDate.now().atStartOfDay()
		val lastCheck = data.maxOfOrNull { it.lastCheck } ?: LocalDateTime.now()
		val periodSeconds = Duration.between(firstCheck, lastCheck).seconds.coerceAtLeast(1)
		val imageWidth = 600
		val minBarWidth = 10
		val timeSlices = dataPoints.coerceIn(10, imageWidth / minBarWidth)
		val timeSliceSeconds = (periodSeconds.toDouble() / timeSlices).roundToInt()
		
		val graph = IntArray(timeSlices) { 1 }
		errors.forEach {
			val secondsSinceStart = Duration.between(firstCheck, it.dateTime).seconds
			val index = (secondsSinceStart.toFloat() / timeSliceSeconds).roundToInt().coerceIn(0, timeSlices - 1)
			graph[index] = 0
		}
		val colorMap = { v: Int ->
			when (v) {
				0 -> Color.getHSBColor(0f, 0.7f, 0.8f)
				1 -> Color.getHSBColor(0.33f, 0.5f, 0.7f)
				else -> Color.gray
			}
		}
		val image = imageService.lineGraph(600, 50, graph, colorMap)
		return imageService.image2Response(image)
	}
	
}
