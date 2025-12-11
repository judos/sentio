package ch.judos.sentio.services.helper

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import io.quarkus.logging.Log
import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.cancellation.CancellationException

class TelegramBot(
	token: String
) {
	
	var chatId = CompletableDeferred<Long>().apply {
		this.invokeOnCompletion { throwable ->
			if (throwable is CancellationException) {
				Log.info("stop polling for bot $botName")
				bot.stopPolling()
				return@invokeOnCompletion
			}
			Log.info("completed searching chatId", throwable)
		}
	}
	
	val bot = bot {
		this.token = token
		logLevel = LogLevel.All()
		dispatch {
			text {
				Log.info("Bot $botName received message ${message.text} in chat ${message.chat.id}")
				setChat(message.chat.id)
			}
			command("start") {
				Log.info("Bot $botName start in chat ${message.chat.id}")
				setChat(message.chat.id)
			}
		}
	}.apply { startPolling() }
	
	val botName: String = bot.getMe().get().username.apply {
		Log.info("Bot $this initialized")
	} ?: throw Exception("Failed to fetch bot username")
	
	suspend fun sendMessage(text: String) {
		if (!chatId.isCompleted) {
			Log.warn("Bot $botName has no chat id set, cannot send message")
			return
		}
		bot.sendMessage(
			chatId = ChatId.fromId(chatId.await()),
			text = text
		)
	}
	
	suspend fun setChat(id: Long) {
		if (!chatId.isActive) {
			Log.warn("Bot $botName already has chat id ${chatId.await()}, ignoring new id $id")
			return
		}
		chatId.complete(id)
		Log.info("Bot $botName Chat id set to $id")
		sendMessage("Connected to this chat with Id $id")
	}
}
