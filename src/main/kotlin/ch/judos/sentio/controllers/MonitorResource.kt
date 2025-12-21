package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QData
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.entities.QWebsiteConfig
import ch.judos.sentio.entities.WebsiteConfig
import ch.judos.sentio.services.monitors.Monitor
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
class MonitorResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager
) {
	val qWebsite = QWebsite.website
	val qConfig = QWebsiteConfig.websiteConfig
	val qData = QData.data
	val qMonitorError = QMonitorError.monitorError
	
	@DELETE
	@Path("/{id}/{configId}")
	@Transactional
	fun delete(id: Long, configId: Long): Response {
		val config = query.selectFrom(qConfig).where(qConfig.website.id.eq(id)
			.and(qConfig.id.eq(configId))).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		
		query.delete(qData).where(qData.config.id.eq(configId)).execute()
		query.delete(qMonitorError).where(qMonitorError.config.id.eq(configId)).execute()
		entityManager.remove(config)
		return Response.noContent().build()
	}
	
	@POST
	@Path("/{id}/{monitorKey}")
	@Transactional
	fun addUpdate(id: Long, monitorKey: String, updatedConfig: WebsiteConfig): Response {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		val monitor = Monitor.monitors.find { it.getKey() == monitorKey }
			?: return Response.status(NOT_FOUND).build()
		val config = website.configs.find { it.monitor == monitorKey }
			?: monitor.getDefaultConfigFor(website)
		config.updateFrom(updatedConfig)
		entityManager.persist(config)
		return Response.noContent().build()
	}
	
}
