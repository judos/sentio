package ch.judos.sentio.extensions

import jakarta.ws.rs.core.Response


fun ResponseFound(location: String): Response {
	return Response.status(Response.Status.FOUND)
		.header("Location", location)
		.build()
}

fun ResponseError(msg: String): Response {
	return Response.status(Response.Status.BAD_REQUEST)
		.entity(mapOf("error" to msg))
		.build()
}
