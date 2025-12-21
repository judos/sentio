package ch.judos.sentio.controllers

import ch.judos.sentio.config.GlobalTemplateVars
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.WebsiteConfig
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

@Path("")
@Produces(MediaType.TEXT_HTML)
class UiResource @Inject constructor(
	@Location("overview.html")
	var overview: Template,
	@Location("website-add.html")
	var websiteAdd: Template,
	@Location("website-details.html")
	var websiteDetails: Template,
	@Location("website-monitor.html")
	var websiteMonitor: Template,
	val query: JPAQueryFactory,
	val monitorDataService: MonitorDataService
) {
	val qWebsite = QWebsite.website
	val qErrors = QMonitorError.monitorError
	
	
	@GET
	@Path("/")
	fun overview(): Response {
		val loggedIn = GlobalTemplateVars.username() != null
		return Response.status(Response.Status.FOUND)
			.header("Location", if (loggedIn) "/website" else "/login")
			.build()
	}
	
	@GET
	@Path("/website")
	fun websites(@CookieParam("sentio_dateRange") daysStr: String?): String {
		val days = daysStr?.toIntOrNull() ?: 7
		val websites = query.selectFrom(qWebsite).fetch()
		val uptime = monitorDataService.getUptimePercentage(null, days)
			.mapValues { floor(it.value).toInt() }
		val colors = uptime.mapValues {
			when {
				it.value >= 99 -> "hsl(120, 30%, 70%);"
				else -> "hsl(${it.value * 1.2}, 30%, 70%);"
			}
		}
		return overview.data("websites", websites)
			.data("uptime", uptime)
			.data("colors", colors)
			.render()
	}
	
	@GET
	@Path("/website/new")
	fun websiteAdd(): String = websiteAdd.render()
	
	@GET
	@Path("/website/{id}")
	fun websiteDetails(id: Long): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitors = Monitor.monitors.filter { monitor ->
			website.configs.none { it.monitor == monitor.getKey() }
		}
		val monitorByKey = Monitor.monitors.associateBy { it.getKey() }
		val configs = website.configs.sortedBy { it.monitor }
		return Response.ok(
			websiteDetails.data("website", website)
				.data("configs", configs)
				.data("monitorByKey", monitorByKey)
				.data("monitors", monitors)
				.render()
		).build()
	}
	
	@GET
	@Path("/website/{id}/{configId}")
	fun websiteDetails(
		id: Long, configId: String,
		@CookieParam("sentio_dateRange") daysStr: String?
	): Response {
		val days = daysStr?.toLongOrNull() ?: 7L
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		
		val config: WebsiteConfig
		val monitor: Monitor
		if (configId.toLongOrNull() != null) {
			config = website.configs.firstOrNull { it.id == configId.toLongOrNull() }
				?: return Response.status(NOT_FOUND).build()
			monitor = Monitor.monitors.firstOrNull { it.getKey() == config.monitor }
				?: return Response.status(NOT_FOUND).build()
		}
		else {
			monitor = Monitor.monitors.firstOrNull { it.getKey() == configId }
				?: return Response.status(NOT_FOUND).build()
			config = monitor.getDefaultConfigFor(website)
		}
		val errors = query.selectFrom(qErrors).where(
			qErrors.website.eq(website),
			qErrors.config.id.eqOrNull(configId.toLongOrNull()),
			qErrors.dateTime.goe(LocalDateTime.now().minusDays(days))
		).fetch()
		return Response.ok(
			websiteMonitor.data("website", website)
				.data("monitor", monitor)
				.data("errors", errors)
				.data("config", config).render()
		).build()
	}
}
