package ch.judos.sentio.extensions

import jakarta.ws.rs.core.Response


fun ResponseFound(location: String): Response {
	return Response.status(Response.Status.FOUND)
		.header("Location", location)
		.build()
}
