package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.Monitored
import ch.judos.sentio.model.MonitorField
import io.quarkus.logging.Log
import io.quarkus.qute.TemplateData
import kotlinx.serialization.Serializable
import java.io.File

@TemplateData
class FileMonitor(
) : Monitor<FileMonitor.Settings> {
	
	override val settingsSerializer = Settings.serializer()
	
	@Serializable
	class Settings {
		lateinit var path: String
		lateinit var cmd: String
		lateinit var expectedValue: String
	}
	
	override fun checkAndReturnError(config: Monitored): String? {
		return try {
			val settings = getSettings(config)
			var content = File(settings.path).readText()
			content = applyCmds(settings.cmd, content)
			if (content == settings.expectedValue) null else "Unexpected value: $content"
		} catch (e: Exception) {
			Log.warn("Monitor Check Failed", e)
			val c = e::class.simpleName!!.removeSuffix("Exception")
			val msg = e.message ?: "Unknown error"
			"$c: $msg"
		}
	}
	
	fun applyCmds(cmds: String, content: String): String {
		var currentContent = content
		cmds.split(";").forEach { cmd ->
			currentContent = applyCmd(cmd, currentContent)
		}
		return currentContent
	}
	
	fun applyCmd(cmd: String, content: String): String {
		// if brackets available, extract content inside brackets
		// e.g. line(1)
		var args: List<String> = emptyList()
		var baseCmd = cmd
		if (cmd.contains("(") && cmd.endsWith(")")) {
			baseCmd = cmd.substringBefore("(")
			args = cmd.substringAfter("(").substringBefore(")").split(",")
		}
		return when (baseCmd) {
			"line" -> {
				val lineNum = args.getOrNull(0)?.toIntOrNull()
					?: throw IllegalArgumentException("line command requires valid line number")
				val lines = content.lines()
				if (lineNum < 1 || lineNum > lines.size) throw IllegalArgumentException(
					"line number out of range")
				lines[lineNum - 1]
			}
			
			"split" -> {
				val delimiter =
					args.getOrNull(0) ?: throw IllegalArgumentException("split command requires a delimiter")
				val index = args.getOrNull(1)?.toIntOrNull()
					?: throw IllegalArgumentException("split command requires valid index")
				val parts = content.split(delimiter)
				if (index < 0 || index >= parts.size) throw IllegalArgumentException(
					"split index out of range")
				parts[index]
			}
			
			"multiply" -> {
				val factor = args.getOrNull(0)?.toDoubleOrNull()
					?: throw IllegalArgumentException("mul command requires valid factor")
				val number = content.toDoubleOrNull()
					?: throw IllegalArgumentException("content is not a valid number for mul command")
				(number * factor).toString()
			}
			
			"inRange" -> {
				val min = args.getOrNull(0)?.toDoubleOrNull()
					?: throw IllegalArgumentException("inRange command requires valid min value")
				val max = args.getOrNull(1)?.toDoubleOrNull()
					?: throw IllegalArgumentException("inRange command requires valid max value")
				val number = content.toDoubleOrNull()
					?: throw IllegalArgumentException("content is not a valid number for inRange command")
				if (number in min..max) "true" else "false"
			}
			
			else -> throw IllegalArgumentException("unknown command: $baseCmd")
		}
		
	}
	
	override fun getKey() = "file"
	
	override fun getDefaultAlertIfFailingForMin() = 30
	
	override fun getDefaultCheckEveryMin() = 5
	
	override fun getName() = "File"
	
	override fun getFields(): List<MonitorField> {
		return listOf(
			MonitorField("path", "File path", "text"),
			MonitorField("cmd", "Parse command", "text"),
			MonitorField("expectedValue", "Expected value", "text")
		)
	}
}
