package ch.judos.sentio.config.auth

import ch.judos.sentio.extensions.urlencode
import ch.judos.sentio.services.JwtService
import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger

@Provider
@Priority(Priorities.AUTHENTICATION) // run early in the chain
class AuthFilter(
		private val jwtService: JwtService
) : ContainerRequestFilter {
	
	@Inject
	lateinit var sentioIdentity: SentioIdentity
	
	private val log: Logger = Logger.getLogger(this::class.java)
	
	override fun filter(requestContext: ContainerRequestContext) {
		val path = requestContext.uriInfo.path
		val isRoutePublic = path in arrayOf("/login")
		try {
			val jwt = requestContext.cookies["sentio_jwt"]?.value
			val claims = jwt?.let { jwtService.parseToken(jwt) }
			if (claims != null) {
				sentioIdentity.userId = (claims.payload["userId"] as Int).toLong()
				sentioIdentity.username = claims.payload.subject
				log.info("authenticated user: ${claims.payload.subject}")
				return
			}
			if (isRoutePublic) {
				return
			}
			abort(requestContext, "Please login")
		} catch (e: Exception) {
			log.warn(e.message)
			if (isRoutePublic) {
				return
			}
			abort(requestContext, "Your login expired, please login again")
		}
	}
	
	private fun abort(ctx: ContainerRequestContext, message: String) {
		ctx.abortWith(
			Response.status(Response.Status.FOUND)
				.header("Location", "/login?error=${message.urlencode()}")
				.header("Set-Cookie", "sentio_jwt=; SameSite=Strict; Max-Age=0")
				.build()
		)
	}
}
