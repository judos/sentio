package ch.judos.sentio.model

import ch.judos.sentio.entities.Data
import java.awt.Color
import java.awt.image.BufferedImage
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class DataPeriod(
		val days: Int
) {
	
	val slices: Int = mapOf(1 to 24, 7 to 14, 30 to 30)[days]!!
	val startTime = LocalDateTime.now().minusDays(days.toLong())
	val startTimeS = startTime.toEpochSecond(ZoneOffset.UTC)
	val nowS = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
	val sliceDurationS = (nowS - startTimeS).toDouble() / slices
	
	val checkedPer = DoubleArray(slices) { 0.0 }
	val successPer = DoubleArray(slices) { 0.0 }
	
	fun addData(data: Data) {
		var start = timeToSliceIndex(toEpoch(data.date.atTime(data.firstCheck)))
		val end = timeToSliceIndex(toEpoch(data.date.atTime(data.lastCheck)))
		val period = end - start
		start = start.coerceAtLeast(0.0)
		do {
			val index = start.toInt()
			if (index in 0..<slices) {
				val checked = end.coerceAtMost(ceil(start + 0.001)) - start
				val successRate = data.succeeded.toDouble() / (data.succeeded + data.failed)
				checkedPer[index] += checked
				successPer[index] += checked * successRate
			}
			start = ceil(start + 0.001)
		} while (start < end && start < slices)
	}
	
	private fun toEpoch(time: LocalDateTime): Long {
		return time.toEpochSecond(ZoneOffset.UTC)
	}
	
	private fun timeToSliceIndex(timeS: Long): Double {
		return (timeS - startTimeS) / sliceDurationS
	}
	
	private fun sliceIndexToTime(index: Double): Long {
		return startTimeS + (index * sliceDurationS).roundToLong()
	}
	
	fun toImage(width: Int, height: Int): BufferedImage {
		val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val g2d = image.createGraphics()
		g2d.background = Color.gray
		g2d.clearRect(0, 0, width, height)
		val lineWidth = width.toDouble() / checkedPer.size
		val rectWidth = ceil(lineWidth).toInt()
		for (i in 0 until checkedPer.size) {
			val h = (checkedPer[i] * height).roundToInt()
			val success = successPer[i] / checkedPer[i]
			g2d.color = color(success)
			val x = (i * lineWidth).roundToInt()
			g2d.fillRect(x, height - h, rectWidth, h)
		}
		g2d.color = Color(0, 0, 0, 50)
		for (i in 0 until checkedPer.size) {
			val x = (i * lineWidth).roundToInt()
			g2d.drawLine(x, height, x, (height * 0.7).roundToInt())
		}
		g2d.dispose()
		return image
	}
	
	private fun color(successPer: Double): Color {
		val f = successPer.toFloat()
		return Color.getHSBColor(
			0f + f * 0.33f,
			0.7f - f * 0.2f,
			0.8f - f * 0.1f
		)
	}
}
