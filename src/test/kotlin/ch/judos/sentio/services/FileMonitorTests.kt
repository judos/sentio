package ch.judos.sentio.services

import ch.judos.sentio.services.monitors.FileMonitor
import io.quarkus.logging.Log
import org.junit.jupiter.api.Test

class FileMonitorTests {
	
	@Test
	fun `test commands`() {
		val monitor = FileMonitor()
		
		val input = "0.00 0.90 0.00 1/713 2658"
		val cmd="line(1);split( ,1);multiply(100);inRange(0,90)"
		
		val out = monitor.applyCmds(cmd, input)
		
		Log.info(out)
		
	}
}
