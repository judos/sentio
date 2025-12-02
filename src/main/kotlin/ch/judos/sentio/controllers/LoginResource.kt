package ch.judos.sentio.controllers

import ch.judos.sentio.entities.User
import ch.judos.sentio.extensions.ResponseFound
import ch.judos.sentio.extensions.urlencode
import ch.judos.sentio.services.JwtService
import ch.judos.sentio.services.PasswordService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.runtime.configuration.ConfigUtils
import io.vertx.ext.web.RoutingContext
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger


@Path("/login")
class LoginResource(
		@Location("login.html") var loginTemplate: Template,
		private val em: EntityManager,
		private val jwtService: JwtService,
		@ConfigProperty(name = "sentio.jwt.expiration")
		private var jwtExpirationSeconds: Long
) {
	
	private val log: Logger = Logger.getLogger(LoginResource::class.java)
	
	@GET
	@Produces("text/html")
	fun loginPage(@Context ctx: RoutingContext, @Context securityContext: SecurityContext): Response {
		val principal = securityContext.userPrincipal
		if (principal != null) {
			// Already logged in
			return ResponseFound("/")
		}
		val error = ctx.request().getParam("error")
		return Response.ok(loginTemplate.data("error", error).render()).build()
	}
	
	@POST
	@Transactional
	fun login(
			@FormParam("username") username: String,
			@FormParam("password") password: String,
	): Response {
		val user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User::class.java)
			.setParameter("username", username)
			.resultList
			.firstOrNull()
		if (user != null && PasswordService.verifyPw(password, user.password)) {
			val jwt = jwtService.createToken(user)
			val profiles = ConfigUtils.getProfiles()
			val secure = if (profiles.any { it == "dev" }) "" else "; Secure"
			val cookieValue = "sentio_jwt=$jwt; SameSite=Strict; Max-Age=$jwtExpirationSeconds$secure"
			return Response.seeOther(java.net.URI("/"))
				.header("Set-Cookie", cookieValue)
				.build()
		}
		val m = "Login failed".urlencode()
		return Response.seeOther(java.net.URI("/login?error=$m")).build()
	}
}
