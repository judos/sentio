package ch.judos.sentio.controllers

import ch.judos.sentio.entities.Website
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/api/websites")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WebsiteResource {
	@GET
	fun fetchAll(): List<Website> = Website.listAll()
	
	@POST
	@Transactional
	fun create(website: Website): Response {
		website.id = null // ensure new entity
		website.persist()
		return Response.created(URI.create("/websites/${website.id}")).entity(website).build()
	}
	
	@PUT
	@Path("/{id}")
	@Transactional
	fun update(@PathParam("id") id: Long, updated: Website): Response {
		val website = Website.findById(id)
			?: return Response.status(Response.Status.NOT_FOUND).build()
		website.name = updated.name
		website.url = updated.url
		website.lastSuccess = updated.lastSuccess
		return Response.ok(website).build()
	}
	
	@DELETE
	@Path("/{id}")
	@Transactional
	fun delete(@PathParam("id") id: Long): Response {
		val deleted = Website.deleteById(id)
		return if (deleted) Response.noContent().build()
		else Response.status(Response.Status.NOT_FOUND).build()
	}
}
