package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.Website
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/api/websites")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WebsiteResource (
	val query: JPAQueryFactory,
	val entityManager: EntityManager
){
	val qWebsite = QWebsite.website
	
	@GET
	fun fetchAll(): List<Website> = query.selectFrom(qWebsite).fetch()
	
	@POST
	@Transactional
	fun create(website: Website): Response {
		website.id = null // ensure new entity
		entityManager.persist(website)
		return Response.created(URI.create("/websites/${website.id}")).entity(website).build()
	}
	
	@PUT
	@Path("/{id}")
	@Transactional
	fun update(@PathParam("id") id: Int, updated: Website): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(Response.Status.NOT_FOUND).build()
		website.name = updated.name
		website.url = updated.url
		entityManager.persist(website)
		return Response.ok(website).build()
	}
	
	@DELETE
	@Path("/{id}")
	@Transactional
	fun delete(@PathParam("id") id: Int): Response {
		val affected = query.delete(qWebsite).where(qWebsite.id.eq(id)).execute()
		return if (affected == 1L) Response.noContent().build()
		else Response.status(Response.Status.NOT_FOUND).build()
	}
}
