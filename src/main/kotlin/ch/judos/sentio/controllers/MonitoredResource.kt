package ch.judos.sentio.controllers

import ch.judos.sentio.entities.Monitored
import ch.judos.sentio.entities.QData
import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QMonitored
import ch.judos.sentio.services.monitors.Monitor
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.NOT_FOUND

@Path("/api/monitored")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MonitoredResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager
) {
	
	val qMonitored = QMonitored.monitored!!
	val qData = QData.data!!
	val qMonitorError = QMonitorError.monitorError!!
	
	@DELETE
	@Path("/{monitoredId}")
	@Transactional
	fun delete(monitoredId: Long): Response {
		val config = query.selectFrom(qMonitored).where(qMonitored.id.eq(monitoredId)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		
		query.delete(qData).where(qData.monitored.id.eq(monitoredId)).execute()
		query.delete(qMonitorError).where(qMonitorError.monitored.id.eq(monitoredId)).execute()
		entityManager.remove(config)
		return Response.noContent().build()
	}
	
	@POST
	@Path("")
	@Transactional
	fun createMonitored(monitored: Monitored): Response {
		Monitor.byKey(monitored.monitor)
			?: return Response.status(NOT_FOUND).entity("Monitor type not found").build()
		entityManager.persist(monitored)
		return Response.ok(mapOf("id" to monitored.id)).build()
	}
	
	@POST
	@Path("/{monitoredId}")
	@Transactional
	fun updateMonitored(monitoredId: Long, updatedConfig: Monitored): Response {
		val monitored = query.selectFrom(qMonitored).where(qMonitored.id.eq(monitoredId)).fetchOne()
			?: return Response.status(NOT_FOUND).build()
		monitored.updateFrom(updatedConfig)
		entityManager.persist(monitored)
		return Response.ok(mapOf("id" to monitored.id)).build()
	}
}
