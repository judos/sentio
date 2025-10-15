package ch.judos.sentio.services

import ch.judos.sentio.extensions.toJpegByteArr
import ch.judos.sentio.extensions.toWebpByteArr
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import java.awt.image.BufferedImage

@ApplicationScoped
class ImageService(
	var configService: ConfigService
) {
	
	fun image2Response(image: BufferedImage): Response {
		val format = configService.getStr("image_format")
		val quality = configService.getFloat("image_quality")
		val data = when (format) {
			"webp" -> "image/webp" to image.toWebpByteArr(quality)
			"jpg" -> "image/jpeg" to image.toJpegByteArr(quality)
			else -> throw RuntimeException("Unsupported image format: $format")
		}
		return Response.ok(data.second).type(data.first).build()
	}
	
}
