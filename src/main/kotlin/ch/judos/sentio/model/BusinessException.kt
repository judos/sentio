package ch.judos.sentio.model

class BusinessException(
	val key: String,
	override val message: String,
	val details: Any? = null
) : RuntimeException(message)
