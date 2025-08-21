package ch.judos.sentio.controllers

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("")
class UiResource {
    @Inject
    @Location("hello.html")
    lateinit var hello: Template

    @Inject
    @Location("add-website.html")
    lateinit var addWebsite: Template

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_HTML)
    fun hello(): String = hello.data("name", "Quarkus-Nutzer").render()

    @GET
    @Path("/add-website")
    @Produces(MediaType.TEXT_HTML)
    fun showAddWebsite(): String = addWebsite.render()
}
