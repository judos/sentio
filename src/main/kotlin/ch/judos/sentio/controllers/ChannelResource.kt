package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QChannel
import ch.judos.sentio.services.EncryptionService
import ch.judos.sentio.services.TelegramService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/channel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
open class ChannelResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager,
		val telegramService: TelegramService,
		val encryptionService: EncryptionService
) {
	val qChannel = QChannel.channel
	
	@POST
	@Transactional
	open fun create(data: CreateBot): Response {
		val name = telegramService.create(data.token)
		return Response.ok(mapOf("msg" to "ok")).build()
	}
	
	@POST
	@Path("/{id}/test")
	open fun test(@PathParam("id") id: Long): Response {
		val channel = query.selectFrom(qChannel).where(qChannel.id.eq(id)).fetchOne()
			?: return Response.status(Response.Status.NOT_FOUND).build()
		telegramService.sendMessage(channel, "This is a test message from Sentio.")
		return Response.ok(mapOf("msg" to "ok")).build()
	}
	
	@DELETE
	@Path("/{id}")
	@Transactional
	open fun delete(id: Long): Response {
		query.delete(qChannel).where(qChannel.id.eq(id)).execute()
		return Response.noContent().build()
	}
	
	class CreateBot {
		lateinit var token: String
	}
	
}
