package ch.judos.sentio.services

import ch.judos.sentio.extensions.errorDescription
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.types.TelegramBotResult.Error
import io.quarkus.logging.Log
import io.quarkus.runtime.ShutdownEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class TelegramService(
	@ConfigProperty(name = "sentio.telegram.bot-token")
	private var botToken: String
) {
	
	var lastUpdate: Long = 0
	
	val bot = bot {
		logLevel = LogLevel.All()
		token = botToken
		dispatch {
			text {
				Log.info("mirroring message")
				bot.sendMessage(ChatId.fromId(message.chat.id), text = text)
			}
		}
	}.apply { startPolling() }
	
	fun test() {
		val updates = bot.getUpdates()
		if (updates is Error) {
			throw RuntimeException(updates.errorDescription())
		}
		val list = updates.get()
		val chatIds = mutableMapOf<Long, MutableSet<String>>()
		for (update in list) {
			lastUpdate = update.updateId
			update.message?.let { msg ->
				val chatId = msg.chat.id
				val set = chatIds.getOrPut(chatId) { mutableSetOf() }
				msg.authorSignature?.let { set += "authorSignature: $it" }
				msg.caption?.let { set += "caption: $it" }
				msg.text?.let { set += "text: $it" }
				msg.from?.username?.let { set += "from: $it" }
				msg.chat.username?.let { set += "chat.username: $it" }
				msg.chat.description?.let { set += "chat.description: $it" }
				msg.chat.firstName?.let { set += "chat.firstName: $it" }
				
				// val chatId = ChatId.fromId(msg.chat.id)
				// val text = msg.text ?: "<no text>"
			}
		}
		
		Log.info(chatIds.toString())
		
		// if (list.isEmpty()) throw RuntimeException("No updates found")
		// val msg1 = list.first()
		// val chatId = ChatId.fromId(msg1.message!!.chat.id)
		// val result = bot.sendMessage(chatId, "Hello from Sentio!")
		// val ok = result.isSuccess
	}
	
	fun onStop(@Observes ev: ShutdownEvent) {
		Log.info("stopping polling...")
		bot.stopPolling()
		Log.info("stopped polling")
	}
}
