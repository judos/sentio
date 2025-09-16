package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.WebsiteConfig

interface MonitorService {
	
	/** run check for specific website
	 * @return Triple<Boolean, String?, Any?> - 1. successful, 2. optional message (e.g. error details),
	 * 3. value to display in diagram
	 */
	fun check(config: WebsiteConfig): Triple<Boolean, String?, Long>
	
	/** unique value for entries in db */
	fun getKey(): String
	fun getDefaultAlertIfFailingForMin(): Int
	fun getDefaultCheckEveryMin(): Int
	fun getName(): String
}
