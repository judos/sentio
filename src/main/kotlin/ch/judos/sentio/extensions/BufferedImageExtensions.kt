package ch.judos.sentio.extensions

import io.quarkus.logging.Log
import jakarta.ws.rs.core.Response
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.MemoryCacheImageOutputStream


fun BufferedImage.toResponse(quality: Float, format: String): Response {
	try {
		val data = when (format) {
			"webp" -> "image/webp" to toWebpByteArr(quality)
			"jpg" -> "image/jpeg" to toJpegByteArr(quality)
			else -> throw RuntimeException("Unsupported image format: $format")
		}
		return Response.ok(data.second)
			.header("Cache-Control", "no-cache")
			.type(data.first).build()
	} catch (e: Throwable) {
		if (format != "jpg") {
			// Fallback to JPEG
			Log.warn("Failed to convert image to $format, falling back to jpg: ${e.message}")
			return toResponse(quality, "jpg")
		}
		throw RuntimeException("Failed to convert image to $format", e)
	}
}

/** @param quality Set compression quality (0.0f = lowest, 1.0f = highest) */
fun BufferedImage.toWebpByteArr(quality: Float): ByteArray {
	return toImageByteArray("webp") { param ->
		if (param.canWriteCompressed()) {
			param.compressionMode = ImageWriteParam.MODE_EXPLICIT
			param.compressionType = "Lossy"
			param.compressionQuality = quality
		}
	}
}

/** @param quality Set compression quality (0.0f = lowest, 1.0f = highest) */
fun BufferedImage.toJpegByteArr(quality: Float): ByteArray {
	return toImageByteArray("jpg") { param ->
		if (param.canWriteCompressed()) {
			param.compressionMode = ImageWriteParam.MODE_EXPLICIT
			param.compressionQuality = quality // <-- set quality (0.0 to 1.0)
		}
	}
}

fun BufferedImage.toImageByteArray(format: String,
		setParams: (ImageWriteParam) -> Unit): ByteArray {
	val writer: ImageWriter = ImageIO.getImageWritersByFormatName(format).next()
	val param: ImageWriteParam = writer.defaultWriteParam
	setParams(param)
	val baos = ByteArrayOutputStream()
	MemoryCacheImageOutputStream(baos).use {
		writer.output = it
		writer.write(null, IIOImage(this, null, null), param)
	}
	return baos.toByteArray()
}
