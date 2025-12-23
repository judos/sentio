package ch.judos.sentio.services

import ch.judos.sentio.entities.QChannel
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
open class ChannelService(
		private val telegramService: TelegramService,
		private val query: JPAQueryFactory,
) {
	
	private val qChannel = QChannel.channel!!
	
	fun sendAlert(message: String) {
		val channel = query.selectFrom(qChannel).fetch()
		channel.forEach {
			when (it.type) {
				"telegram" -> telegramService.sendMessage(it, message)
				else -> Log.error("unknown channel type: ${it.type}")
			}
		}
	}
}
