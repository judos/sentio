package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.services.monitors.MonitorService
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.NOT_FOUND

@Path("")
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
) {
	
	val qWebsite = QWebsite.website
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	fun overview(): String {
		val websites = query.selectFrom(qWebsite).fetch()
		return overview.data("websites", websites)
			.render()
	}
	
	@GET
	@Path("/website/new")
	@Produces(MediaType.TEXT_HTML)
	fun websiteAdd(): String = websiteAdd.render()
	
	@GET
	@Path("/website/{id}")
	@Produces(MediaType.TEXT_HTML)
	fun websiteDetails(id: Long): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitors = MonitorService.monitors.filter { monitor ->
			website.configs.none { it.monitor == monitor.getKey() }
		}
		val configs = website.configs
		return Response.ok(websiteDetails.data("website", website)
			.data("configs", configs)
			.data("monitors", monitors)
			.data("showDays", 14)
			.render()).build()
	}
	@GET
	@Path("/website/{id}/{monitorKey}")
	@Produces(MediaType.TEXT_HTML)
	fun websiteDetails(id: Long, monitorKey: String): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitor = MonitorService.monitors.firstOrNull { it.getKey() == monitorKey }
			?: return Response.status(NOT_FOUND).build()
		val config = website.configs.firstOrNull { it.monitor == monitorKey }
			?: monitor.getDefaultConfigFor(website)
		return Response.ok(websiteMonitor.data("website", website)
			.data("monitor", monitor)
			.data("config", config).render()).build()
	}
}
