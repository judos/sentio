package ch.judos.sentio.config

import ch.judos.sentio.model.BusinessException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider


@Provider
class BusinessExceptionMapper : ExceptionMapper<BusinessException> {
	override fun toResponse(ex: BusinessException): Response {
		return ex.toResponse()
	}
}
