package ch.judos.sentio.controllers

import ch.judos.sentio.config.GlobalTemplateVars
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("")
@Produces(MediaType.TEXT_HTML)
class UiResource @Inject constructor(
) {
	
	@GET
	@Path("/")
	fun overview(): Response {
		val loggedIn = GlobalTemplateVars.username() != null
		return Response.status(Response.Status.FOUND)
			.header("Location", if (loggedIn) "/monitored" else "/login")
			.build()
	}
	
}
