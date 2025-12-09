package ch.judos.sentio.controllers

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response


@Path("/notification")
@Produces(MediaType.TEXT_HTML)
class NotificationUiResource(
		@Location("notification-overview.html")
		var overview: Template,
		@Location("notification-edit.html")
		var edit: Template,
) {
	
	@GET
	@Path("/")
	fun overview(): Response {
		return Response.ok(overview.render()).build()
	}
	
	@GET
	@Path("/{id}")
	fun edit(): Response {
		return Response.ok(edit.render()).build()
	}
	
}
