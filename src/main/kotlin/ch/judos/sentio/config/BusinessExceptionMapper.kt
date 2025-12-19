package ch.judos.sentio.config

import ch.judos.sentio.model.BusinessException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider


@Provider
class BusinessExceptionMapper : ExceptionMapper<BusinessException> {
	override fun toResponse(ex: BusinessException): Response {
		return Response.status(400).entity(
			mapOf("key" to ex.key, "message" to ex.message, "details" to ex.details)
		).build()
	}
}
