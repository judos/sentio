package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsite
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("")
class UiResource @Inject constructor(
	@Location("overview.html")
	var overview: Template,
	@Location("website-add.html")
	var websiteAdd: Template,
	@Location("website-details.html")
	var websiteDetails: Template,
	val query: JPAQueryFactory,
) {
	
	val qWebsite = QWebsite.website
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	fun overview(): String {
		val websites = query.selectFrom(qWebsite).fetch()
		return overview.data("websites", websites)
			.render()
	}
	
	@GET
	@Path("/website/new")
	@Produces(MediaType.TEXT_HTML)
	fun websiteAdd(): String = websiteAdd.render()
	
	@GET
	@Path("/website/{id}")
	@Produces(MediaType.TEXT_HTML)
	fun websiteDetails(id: Int): String {
		val website = query.selectFrom(qWebsite).where(qWebsite.id.eq(id)).fetchOne()
		return websiteDetails.data("website", website)
			.render()
	}
}
