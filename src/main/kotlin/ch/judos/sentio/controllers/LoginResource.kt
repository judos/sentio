package ch.judos.sentio.controllers

import ch.judos.sentio.entities.User
import ch.judos.sentio.services.PasswordService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.vertx.ext.web.RoutingContext
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response

@Path("/login")
class LoginController @Inject constructor(
		@Location("login.html") var loginTemplate: Template,
		private val em: EntityManager
) {
	@GET
	@Produces("text/html")
	fun loginPage(@Context ctx: RoutingContext): String {
		val error = ctx.request().getParam("error")
		return loginTemplate.data("error", error).render()
	}
	
	@POST
	@Transactional
	fun login(
			@FormParam("username") username: String,
			@FormParam("password") password: String,
			@Context ctx: RoutingContext
	): Response {
		val user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User::class.java)
			.setParameter("username", username)
			.resultList
			.firstOrNull()
		if (user != null && PasswordService.verifyPw(password, user.password)) {
			ctx.session().put("user", user.id)
			return Response.seeOther(java.net.URI("/")).build()
		}
		return Response.seeOther(java.net.URI("/login?error=Login%20fehlgeschlagen")).build()
	}
}
