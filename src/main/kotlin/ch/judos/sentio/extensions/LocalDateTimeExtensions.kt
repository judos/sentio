package ch.judos.sentio.extensions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
fun LocalDateTime.format(): String {
	return format(formatter)
}

private val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
fun LocalDateTime.formatTime(): String {
	return format(formatterTime)
}
