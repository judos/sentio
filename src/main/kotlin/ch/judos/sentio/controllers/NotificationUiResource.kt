package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QNotificationChannel
import com.querydsl.jpa.impl.JPAQueryFactory
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
	@Location("notification-new.html")
	var new: Template,
	@Location("notification-edit.html")
	var edit: Template,
	var query: JPAQueryFactory,
) {
	
	val qChannel = QNotificationChannel.notificationChannel
	
	@GET
	@Path("/")
	fun overview(): Response {
		val channels = query.selectFrom(qChannel).fetch()
		return Response.ok(
			overview
				.data("channels", channels)
				.render()
		).build()
	}
	
	@GET
	@Path("/{id}")
	fun edit(id: Long): Response {
		val channel = query.selectFrom(qChannel).where(qChannel.id.eq(id)).fetchOne()
			?: return Response.status(Response.Status.NOT_FOUND).build()
		return Response.ok(edit.data(
			"channel", channel
		).render()).build()
	}
	
	@GET
	@Path("/new")
	fun new(): Response {
		return Response.ok(new.render()).build()
	}
	
}
