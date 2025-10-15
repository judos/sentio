package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.QWebsiteConfig
import ch.judos.sentio.entities.WebsiteConfig
import ch.judos.sentio.services.monitors.MonitorService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.NOT_FOUND

@Path("/api/website-monitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WebsiteMonitorResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager
) {
	val qWebsite = QWebsite.website
	val qConfig = QWebsiteConfig.websiteConfig
	
	@DELETE
	@Path("/{id}/{monitorKey}")
	@Transactional
	fun delete(id: Long, monitorKey: String): Response {
		val config = query.selectFrom(qConfig).where(qConfig.website.id.eq(id)
			.and(qConfig.monitor.eq(monitorKey))).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		entityManager.remove(config)
		return Response.noContent().build()
	}
	
	@POST
	@Path("/{id}/{monitorKey}")
	@Transactional
	fun addUpdate(id: Long, monitorKey: String, updatedConfig: WebsiteConfig): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitor = MonitorService.monitors.find { it.getKey() == monitorKey }
			?: return Response.status(NOT_FOUND).build()
		val config = website.configs.find { it.monitor == monitorKey }
			?: monitor.getDefaultConfigFor(website)
		config.updateFrom(updatedConfig)
		entityManager.persist(config)
		return Response.noContent().build()
	}
	
}
