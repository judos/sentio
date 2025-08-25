package ch.judos.sentio.controllers

import ch.judos.sentio.entities.Website
import ch.judos.sentio.services.WebsiteCheckService
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
	val hello: Template
) {
	
	@Inject
	@Location("add-website.html")
	lateinit var addWebsite: Template
	
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_HTML)
	fun hello(): String {
		return hello.data("websites", Website.listAll())
			.render()
	}
	
	@GET
	@Path("/add-website")
	@Produces(MediaType.TEXT_HTML)
	fun showAddWebsite(): String = addWebsite.render()
}
