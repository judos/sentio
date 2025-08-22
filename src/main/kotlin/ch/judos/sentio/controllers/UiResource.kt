package ch.judos.sentio.controllers

import ch.judos.sentio.entities.Website
import ch.judos.sentio.services.WebsiteValidationService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.time.Duration
import java.time.format.DateTimeFormatter

@Path("")
class UiResource {
	@Inject
	@Location("hello.html")
	lateinit var hello: Template
	
	@Inject
	@Location("add-website.html")
	lateinit var addWebsite: Template
	
	@Inject
	lateinit var websiteValidationService: WebsiteValidationService
	
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_HTML)
	fun hello(): String {
		val lastUpdate = websiteValidationService.lastUpdate
		val nextUpdate = websiteValidationService.getNextUpdate()
		val formatter = DateTimeFormatter.ofPattern("HH:mm")
		return hello.data(
			"websites", Website.listAll()
		).data("name", "Quarkus-Nutzer")
		 .data("lastUpdate", lastUpdate.format(formatter))
		 .data("nextUpdate", nextUpdate.format(formatter))
		 .render()
	}
	
	@GET
	@Path("/add-website")
	@Produces(MediaType.TEXT_HTML)
	fun showAddWebsite(): String = addWebsite.render()
}
