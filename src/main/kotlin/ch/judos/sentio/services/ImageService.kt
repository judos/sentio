package ch.judos.sentio.services

import ch.judos.sentio.extensions.toResponse
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
	
	fun lineGraph(width: Int, height: Int, data: IntArray, colorMap: (Int) -> Color): BufferedImage {
		val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val g2d = image.createGraphics()
		val lineWidth = width.toDouble() / data.size
		val rectWidth = ceil(lineWidth).toInt()
		
		for (i in 0 until data.size) {
			g2d.color = colorMap(data[i])
			val x = (i * lineWidth).roundToInt()
			g2d.fillRect(x, 0, rectWidth, height)
		}
		g2d.color = Color(0, 0, 0, 50)
		for (i in 0 until data.size) {
			val x = (i * lineWidth).roundToInt()
			g2d.drawLine(x, height, x, (height * 0.7).roundToInt())
		}
		g2d.dispose()
		return image
	}
	
	fun image2Response(image: BufferedImage): Response {
		val format = configService.getStr("image_format")!!
		val quality = configService.getFloat("image_quality")!!
		return image.toResponse(quality, format)
	}
	
}
