package ch.judos.sentio.controllers

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.MemoryCacheImageOutputStream


@Path("/image")
class WebsiteMonitorDataResource {
	
	@GET
	@Path("/hello")
	@Produces("image/jpeg")
	fun generateImage(@QueryParam("quality") quality: Float): Response {
		return try {
			val image = image()
			
			// Convert to byte array
			// val baos = ByteArrayOutputStream()
			// ImageIO.write(bufferedImage, "jpg", baos)
			
			// Prepare JPEG writer
			val writer: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
			val writeParam: ImageWriteParam = writer.defaultWriteParam
			
			// Enable explicit compression
			if (writeParam.canWriteCompressed()) {
				writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
				writeParam.compressionQuality = quality // <-- set quality (0.0 to 1.0)
			}
			
			val baos = ByteArrayOutputStream()
			writer.output = MemoryCacheImageOutputStream(baos)
			writer.write(null, IIOImage(image, null, null), writeParam)
			writer.dispose()
			
			Response.ok(baos.toByteArray())
				.type("image/jpeg")
				.build()
		} catch (e: Exception) {
			Response.serverError().entity("Error generating image").build()
		}
	}
	
	@GET
	@Path("/webp")
	@Produces("image/webp")
	fun getWebP(@QueryParam("quality") quality: Float): Response {
		val image = image()
		
		// Get WebP writer
		val writer = ImageIO.getImageWritersByFormatName("webp").next()
		val param: ImageWriteParam = writer.defaultWriteParam
		
		// Set compression quality (0.0f = lowest, 1.0f = highest)
		if (param.canWriteCompressed()) {
			param.compressionMode = ImageWriteParam.MODE_EXPLICIT
			param.compressionType = "Lossy"
			param.compressionQuality = quality
		}
		
		val baos = ByteArrayOutputStream()
		MemoryCacheImageOutputStream(baos).use {
			writer.output = it
			writer.write(null, IIOImage(image, null, null), param)
		}
		writer.dispose()
		
		return Response.ok(baos.toByteArray())
			.type("image/webp")
			.build()
	}
	
	fun image(): BufferedImage {
		val image = BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB)
		val g2d = image.createGraphics()
		
		// Fill background
		g2d.color = Color.WHITE
		g2d.fillRect(0, 0, 300, 100)
		
		// Draw text
		g2d.color = Color.RED
		g2d.font = Font("Arial", Font.BOLD, 24)
		g2d.drawString("Hello Quarkus!", 50, 60)
		g2d.dispose()
		return image
	}
}
