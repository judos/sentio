package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.Website
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.NOT_FOUND
import java.net.URI

@Path("/api/websites")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WebsiteResource (
	val query: JPAQueryFactory,
	val entityManager: EntityManager
){
	val qWebsite = QWebsite.website
	val qErrors = QMonitorError.monitorError
	
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
	fun update(id: Long, updated: Website): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		website.name = updated.name
		website.url = updated.url
		entityManager.persist(website)
		return Response.ok(website).build()
	}
	
	@DELETE
	@Path("/{id}")
	@Transactional
	fun delete(id: Long): Response {
		// MonitorError entfernen
		query.delete(qErrors).where(qErrors.website.id.eq(id)).execute()
		// Website entfernen (Cascade löscht Configs und Data)
		val affected = query.delete(qWebsite).where(qWebsite.id.eq(id)).execute()
		return if (affected > 0) Response.noContent().build() else Response.status(NOT_FOUND).build()
	}
}
