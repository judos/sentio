package ch.judos.sentio.extensions

import com.github.kotlintelegrambot.types.TelegramBotResult

fun TelegramBotResult.Error.errorDescription(): String {
	return when (this) {
		is TelegramBotResult.Error.HttpError ->
			this.description ?: ("Status " + this.httpCode)
		
		is TelegramBotResult.Error.TelegramApi -> this.description
		is TelegramBotResult.Error.InvalidResponse ->
			this.httpStatusMessage ?: ("Status " + this.httpCode)
		
		is TelegramBotResult.Error.Unknown ->
			this.exception.localizedMessage ?: "Request error"
	}
}
