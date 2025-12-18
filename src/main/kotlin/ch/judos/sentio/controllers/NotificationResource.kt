package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QMonitorError
import ch.judos.sentio.entities.QWebsite
import ch.judos.sentio.extensions.ResponseError
import ch.judos.sentio.services.EncryptionService
import ch.judos.sentio.services.TelegramService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/notification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
open class NotificationResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager,
		val telegramService: TelegramService,
		val encryptionService: EncryptionService
) {
	val qWebsite = QWebsite.website
	val qErrors = QMonitorError.monitorError
	
	@POST
	@Transactional
	open fun create(data: CreateBot): Response {
		val name = telegramService.create(data.token)
			?: return ResponseError("Timeout waiting for bot to receive message.")
		return Response.ok(mapOf("msg" to "ok")).build()
	}
	
	class CreateBot {
		lateinit var token: String
	}
	
}
