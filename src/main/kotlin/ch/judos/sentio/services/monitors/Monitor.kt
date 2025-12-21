package ch.judos.sentio.services.monitors

import ch.judos.sentio.entities.Website
import ch.judos.sentio.entities.WebsiteConfig

interface Monitor {
	
	/** run check for specific website */
	fun checkAndReturnError(config: WebsiteConfig): String?
	
	/** unique value for entries in db */
	fun getKey(): String
	fun getDefaultAlertIfFailingForMin(): Int
	fun getDefaultCheckEveryMin(): Int
	fun getName(): String
	
	fun getDefaultConfigFor(website: Website): WebsiteConfig {
		return WebsiteConfig().also {
			it.website = website
			it.monitor = getKey()
			it.checkEveryMin = getDefaultCheckEveryMin()
			it.alertIfFailingForMin = getDefaultAlertIfFailingForMin()
		}
	}
	
	companion object {
		val monitors = listOf(
			SslExpiryMonitor(),
			ReachabilityMonitor()
		)
	}
}
