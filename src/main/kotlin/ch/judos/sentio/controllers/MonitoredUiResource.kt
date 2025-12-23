package ch.judos.sentio.controllers

import ch.judos.sentio.entities.Monitored
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
import kotlin.math.floor

@Path("/monitored")
@Produces(MediaType.TEXT_HTML)
class MonitoredUiResource @Inject constructor(
	@Location("monitored-list.html")
	var monitoredList: Template,
	// TODO: fix
	@Location("website-monitor.html")
	var websiteMonitor: Template,
	val query: JPAQueryFactory,
	val monitorDataService: MonitorDataService
) {
	val qMonitored = QMonitored.monitored
	val qErrors = QMonitorError.monitorError
	
	@GET
	@Path("")
	fun websites(@CookieParam("sentio_dateRange") daysStr: String?): String {
		val days = daysStr?.toIntOrNull() ?: 7
		val monitored = query.selectFrom(qMonitored).fetch()
		
		// TODO: fix uptime
		val uptime = monitorDataService.getUptimePercentage(null, days)
			.mapValues { floor(it.value).toInt() }
		val colors = uptime.mapValues {
			when {
				it.value >= 99 -> "hsl(120, 30%, 70%);"
				else -> "hsl(${it.value * 1.2}, 30%, 70%);"
			}
		}
		return monitoredList.data("monitored", monitored)
			.data("uptime", uptime)
			.data("colors", colors)
			.render()
	}
	
	// TODO: update path
	@GET
	@Path("/monitored/{id}")
	fun websiteMonitor(
		id: String, @CookieParam("sentio_dateRange") daysStr: String?
	): Response {
		val days = daysStr?.toLongOrNull() ?: 7L
		
		val monitored: Monitored
		val monitor: Monitor<out Any>
		if (id.toLongOrNull() != null) {
			monitored = query.selectFrom(qMonitored).where(qMonitored.id.eq(id.toLong())).fetchOne()
				?: return Response.status(NOT_FOUND).build()
			monitor = Monitor.monitors.firstOrNull { it.getKey() == monitored.monitor }
				?: return Response.status(NOT_FOUND).build()
		}
		else {
			monitor = Monitor.monitors.firstOrNull { it.getKey() == id }
				?: return Response.status(NOT_FOUND).build()
			monitored = monitor.getDefault()
		}
		val errors = query.selectFrom(qErrors).where(
			qErrors.monitored.id.eqOrNull(id.toLongOrNull()),
			qErrors.dateTime.goe(LocalDateTime.now().minusDays(days))
		).fetch()
		return Response.ok(
			websiteMonitor
				.data("monitor", monitor)
				.data("errors", errors)
				.data("config", monitored).render()
		).build()
	}
}
