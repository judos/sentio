package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QMonitored
import ch.judos.sentio.extensions.eqOrNull
import ch.judos.sentio.services.MonitorDataService
import ch.judos.sentio.services.monitors.Monitor
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.NOT_FOUND
import java.time.LocalDateTime

@Path("/monitored")
@Produces(MediaType.TEXT_HTML)
class MonitoredUiResource @Inject constructor(
		@Location("monitored-list.html")
		var monitoredList: Template,
		@Location("monitored-edit.html")
		var monitoredEdit: Template,
		@Location("monitored-details.html")
		var monitoredDetails: Template,
		val query: JPAQueryFactory,
		val monitorDataService: MonitorDataService
) {
	
	val qMonitored = QMonitored.monitored
	val qErrors = QMonitorError.monitorError
	
	@GET
	@Path("")
	fun monitoredList(@CookieParam("sentio_dateRange") daysStr: String?): String {
		val days = daysStr?.toIntOrNull() ?: 7
		val monitored = query.selectFrom(qMonitored).fetch()
		
		val uptime = monitorDataService.getUptimePercentage(null, days)
		val colors = uptime.mapValues {
			when {
				it.value >= 99 -> "hsl(120, 30%, 70%);"
				else -> "hsl(${it.value * 1.2}, 30%, 70%);"
			}
		}
		return monitoredList.data("monitored", monitored)
			.data("uptime", uptime)
			.data("colors", colors)
			.data("monitors", Monitor.monitors)
			.render()
	}
	
	@GET
	@Path("new/{monitor}")
	fun createMonitored(
			monitor: String
	): Response {
		val monitor = Monitor.byKey(monitor)
			?: return Response.status(NOT_FOUND).build()
		val monitored = monitor.getDefault()
		return Response.ok(
			monitoredEdit
				.data("monitored", monitored)
				.data("monitor", monitor)
				.render()
		).build()
	}
	
	@GET
	@Path("edit/{id}")
	fun editMonitored(
			id: Long
	): Response {
		val monitored = query.selectFrom(qMonitored).where(qMonitored.id.eq(id.toLong())).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitor = Monitor.byKey(monitored.monitor)
			?: return Response.status(NOT_FOUND).build()
		return Response.ok(
			monitoredEdit
				.data("monitored", monitored)
				.data("monitor", monitor)
				.render()
		).build()
	}
	
	@GET
	@Path("{id}")
	fun showDetails(
			id: String, @CookieParam("sentio_dateRange") daysStr: String?
	): Response {
		val days = daysStr?.toLongOrNull() ?: 7L
		
		val monitored = query.selectFrom(qMonitored).where(qMonitored.id.eq(id.toLong())).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitor = Monitor.byKey(monitored.monitor)
			?: return Response.status(NOT_FOUND).build()
		val errors = query.selectFrom(qErrors).where(
			qErrors.monitored.id.eqOrNull(id.toLongOrNull()),
			qErrors.dateTime.goe(LocalDateTime.now().minusDays(days))
		).fetch()
		
		return Response.ok(
			monitoredDetails
				.data("monitored", monitored)
				.data("monitor", monitor)
				.data("errors", errors)
				.render()
		).build()
	}
}
