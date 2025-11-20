package ch.judos.sentio.extensions

import io.quarkus.qute.TemplateExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object TemplateExtensions {
	
	@TemplateExtension
	@JvmStatic
	fun LocalDateTime.formatDateTime(): String {
		return format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
	}
	
}
