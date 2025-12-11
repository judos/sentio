package ch.judos.sentio.model

import kotlinx.serialization.Serializable

@Serializable
class TelegramCredentials {
	var token: String = ""
	var chatId: Long = 0
}
