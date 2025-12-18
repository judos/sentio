package ch.judos.sentio.services

import ch.judos.sentio.Sentio
import ch.judos.sentio.entities.NotificationChannel
import ch.judos.sentio.model.TelegramCredentials
import ch.judos.sentio.services.helper.TelegramBot
import io.quarkus.logging.Log
import io.quarkus.runtime.ShutdownEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.persistence.EntityManager
import kotlinx.serialization.json.Json
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ApplicationScoped
open class TelegramService(
		val encryptionService: EncryptionService,
		val entityManager: EntityManager,
) {
	
	val bots = mutableMapOf<String, TelegramBot>()
	
	fun onStop(@Observes ev: ShutdownEvent) {
		Log.info("stopping polling...")
		bots.values.forEach { it.stopPolling() }
		Log.info("stopped polling")
	}
	
	fun create(token: String): String? {
		val bot = bots[token] ?: TelegramBot(token).also { bots[token] = it }
		val future = Sentio.pool.submit<Pair<Long, String?>> {
			val resultFuture = CompletableFuture<Pair<Long, String?>>()
			bot.startPolling { chatId, chatTitle ->
				resultFuture.complete(Pair(chatId, chatTitle))
			}
			resultFuture.join()
		}
		val (chatId, chatTitle) = try {
			future.get(3, TimeUnit.SECONDS)
		} catch (_: TimeoutException) {
			future.cancel(true)
			bot.stopPolling()
			return null
		}
		bot.stopPolling()
		val credentials = Json.encodeToString(
			TelegramCredentials().also {
				it.token = token
				it.chatId = chatId
			}
		)
		entityManager.persist(NotificationChannel().apply {
			this.name = bot.botName + (chatTitle?.let { "- $it" } ?: "")
			this.type = "telegram"
			this.credentials = encryptionService.encrypt(credentials)
		})
		Log.info("bot ${bot.botName} saved with chatId $chatId")
		return bot.botName
	}
}
