package ch.judos.sentio.config

import ch.judos.sentio.model.BusinessException
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class ApiNotFoundMapper : ExceptionMapper<NotFoundException> {
	
	@Context
	lateinit var uriInfo: UriInfo
	
	override fun toResponse(exception: NotFoundException): Response {
		val path = uriInfo.path
		if (path.startsWith("/api/")) {
			return BusinessException(
				key = "endpoint_not_found",
				message = "No endpoint found for the request",
				status = 404
			).toResponse()
		}
		// Let Quarkus handle non-API 404s normally
		throw exception
	}
}
