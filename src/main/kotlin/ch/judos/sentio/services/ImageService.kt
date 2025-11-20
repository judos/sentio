package ch.judos.sentio.services

import ch.judos.sentio.extensions.toJpegByteArr
import ch.judos.sentio.extensions.toWebpByteArr
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.ceil
import kotlin.math.roundToInt

@ApplicationScoped
class ImageService(
	var configService: ConfigService
) {
	
	fun lineGraph(width: Int, height: Int, data: IntArray, colorMap: (Int)-> Color): BufferedImage {
		val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val g2d = image.createGraphics()
		val lineWidth = width.toDouble() / data.size
		val rectWidth = ceil(lineWidth).toInt()
		
		for (i in 0 until data.size) {
			g2d.color = colorMap(data[i])
			val x = (i * lineWidth).roundToInt()
			g2d.fillRect(x, 0, rectWidth, height)
		}
		g2d.dispose()
		return image
	}
	
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
