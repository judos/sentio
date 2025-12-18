package ch.judos.sentio.services

import ch.judos.sentio.entities.NotificationChannel
import ch.judos.sentio.model.TelegramCredentials
import ch.judos.sentio.services.helper.TelegramBot
import io.quarkus.logging.Log
import io.quarkus.runtime.ShutdownEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.persistence.EntityManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

@ApplicationScoped
open class TelegramService(
	val encryptionService: EncryptionService,
	val entityManager: EntityManager,
) {
	
	val bots = mutableListOf<TelegramBot>()
	
	fun onStop(@Observes ev: ShutdownEvent) {
		Log.info("stopping polling...")
		bots.forEach { it.bot.stopPolling() }
		Log.info("stopped polling")
	}
	
	open fun create(token: String): String {
		val bot = TelegramBot(token)
		
		val chatId = runBlocking {
			try {
				Log.info("bot ${bot.botName} created, waiting for chat id...")
				val chatId = withTimeout(3_000) { bot.chatId.await() }
				chatId
			} catch (e: Throwable) {
				Log.warn("failed to create bot chat id", e)
				throw e
			}
		}
		bots.add(bot)
		val credentials = Json.encodeToString(
			TelegramCredentials().also {
				it.token = token
				it.chatId = chatId
			}
		)
		entityManager.persist(NotificationChannel().apply {
			this.name = bot.botName
			this.type = "telegram"
			this.credentials = encryptionService.encrypt(credentials)
		})
		Log.info("bot ${bot.botName} saved with chatId $chatId")
		return bot.botName
	}
}
