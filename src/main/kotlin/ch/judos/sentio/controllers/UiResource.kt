package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.extensions.formatTime
import ch.judos.sentio.services.WebsiteCheckService
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("")
class UiResource @Inject constructor(
	@Location("hello.html")
	var hello: Template,
	@Location("add-website.html")
	var addWebsiteTemplate: Template,
	val query: JPAQueryFactory,
	val websiteCheckService: WebsiteCheckService
) {
	
	val qWebsite = QWebsite.website
	
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_HTML)
	fun hello(): String {
		val websites = query.selectFrom(qWebsite).fetch()
		return hello.data("websites", websites)
			.data("lastUpdate", websiteCheckService.lastUpdate.formatTime())
			.data("nextUpdate", websiteCheckService.getNextUpdate().formatTime())
			.render()
	}
	
	@GET
	@Path("/add-website")
	@Produces(MediaType.TEXT_HTML)
	fun showAddWebsite(): String = addWebsiteTemplate.render()
}
