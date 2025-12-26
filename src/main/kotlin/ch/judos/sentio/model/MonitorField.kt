package ch.judos.sentio.model

import io.quarkus.qute.TemplateData

@TemplateData
data class MonitorField(
		val name: String,
		val text: String,
		val type: String
)
