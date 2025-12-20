package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QChannel
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response


@Path("/channel")
@Produces(MediaType.TEXT_HTML)
class ChannelUiResource(
	@Location("channel-overview.html")
	var overview: Template,
	@Location("channel-new.html")
	var new: Template,
	@Location("channel-edit.html")
	var edit: Template,
	var query: JPAQueryFactory,
) {
	
	val qChannel = QChannel.channel
	
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
