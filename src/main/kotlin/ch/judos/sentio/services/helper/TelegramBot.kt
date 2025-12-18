package ch.judos.sentio.services.helper

import ch.judos.sentio.extensions.errorDescription
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.types.TelegramBotResult
import io.quarkus.logging.Log

class TelegramBot(
		token: String
) {
	
	var isPolling: Boolean = false
		private set
	
	private var onReceivedFromChat: ((Long, String?) -> Unit)? = null
	
	private val bot = bot {
		this.token = token
		dispatch {
			text {
				val chat = message.chat
				onReceivedFromChat?.invoke(chat.id, chat.title ?: chat.firstName)
			}
			command("start") {
				val chat = message.chat
				onReceivedFromChat?.invoke(chat.id, chat.title ?: chat.firstName)
			}
		}
	}
	
	val botName: String = bot.getMe().get().username.apply {
		Log.info("Bot $this initialized")
	} ?: throw Exception("Failed to fetch bot username")
	
	fun sendMessage(text: String, chatId: Long) {
		val result = bot.sendMessage(
			chatId = ChatId.fromId(chatId),
			text = text
		)
		if (result is TelegramBotResult.Error) {
			throw Exception("Failed to send message to chat $chatId: ${result.errorDescription()}")
		}
	}
	
	fun startPolling(onReceivedFromChat: ((Long, String?) -> Unit)?) {
		if (isPolling) throw Exception("Polling already started")
		this.onReceivedFromChat = onReceivedFromChat
		isPolling = true
		bot.startPolling()
	}
	
	fun stopPolling() {
		if (!isPolling) return
		isPolling = false
		bot.stopPolling()
	}
	
}
