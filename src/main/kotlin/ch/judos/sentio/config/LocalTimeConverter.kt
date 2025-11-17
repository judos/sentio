package ch.judos.sentio.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Converter(autoApply = true)
class LocalTimeStringConverter : AttributeConverter<LocalTime, String> {
	val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
	
	override fun convertToDatabaseColumn(attribute: LocalTime?): String? {
		return attribute?.format(formatter)
	}
	
	override fun convertToEntityAttribute(dbData: String?): LocalTime? {
		return dbData?.let { LocalTime.parse(it, formatter) }
	}
}

