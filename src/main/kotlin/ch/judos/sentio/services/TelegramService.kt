package ch.judos.sentio.services

import ch.judos.sentio.extensions.errorDescription
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.TelegramBotResult
import com.github.kotlintelegrambot.types.TelegramBotResult.Error
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TelegramService {
	
	val bot = bot {
		logLevel = LogLevel.All()
		token = "1112227648:AAGz4B8Tt4EU1iVy7uRYNzjjZABWB2kHfto"
	}
	
	fun test() {
		val updates = bot.getUpdates(limit = 1)
		if (updates is Error) {
			throw RuntimeException(updates.errorDescription())
		}
		val list = updates.get()
		if (list.isEmpty()) throw RuntimeException("No updates found")
		val msg1 = list.first()
		val chatId = ChatId.fromId(msg1.message!!.chat.id)
		val result = bot.sendMessage(chatId, "Hello from Sentio!")
		val ok = result.isSuccess
	}
}
