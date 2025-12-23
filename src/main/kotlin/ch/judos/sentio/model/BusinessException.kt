package ch.judos.sentio.model

import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

class BusinessException(
		val key: String,
		override val message: String,
		val details: Any? = null,
		val status: Int = 400,
) : RuntimeException(message) {
	
	fun toResponse(): Response {
		return Response.status(status).entity(
			mapOf("key" to key, "message" to message, "details" to details)
		).type(MediaType.APPLICATION_JSON).build()
	}
	
}
