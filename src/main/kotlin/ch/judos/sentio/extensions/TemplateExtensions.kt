package ch.judos.sentio.extensions

import io.quarkus.qute.TemplateExtension

@TemplateExtension
object TemplateExtensions {
	
	@JvmStatic
	fun lowercase(str: String): String = str.lowercase()
	
}
