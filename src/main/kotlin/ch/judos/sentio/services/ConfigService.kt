package ch.judos.sentio.services

import ch.judos.sentio.entities.Config
import ch.judos.sentio.entities.QConfig
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@ApplicationScoped
open class ConfigService(
	var query: JPAQueryFactory,
	@PersistenceContext
	var entityManager: EntityManager,
	var encryptionService: EncryptionService
) {
	
	val qConfig = QConfig.config
	
	/**
	 * automatically decrypts encrypted items
	 * @return null if no entry is found
	 */
	fun getStr(key: String): String? {
		val entry = query.selectFrom(qConfig).where(qConfig.ckey.eq(key)).fetchOne()
			?: return null
		if (entry.encrypted) {
			return encryptionService.decrypt(entry.value)
		}
		return entry.value
	}
	
	fun getFloat(key: String): Float? {
		return getStr(key)?.toFloat()
	}
	
	fun getToken(key: String): Config {
		return query.selectFrom(qConfig).where(qConfig.ckey.eq(key)).fetchOne()
			?: return Config()
	}
	
	fun store(key: String, value: String) {
		storeIntern(key, value, false)
	}
	
	fun storeEncrypted(key: String, value: String): Config {
		return storeIntern(key, encryptionService.encrypt(value), true)
	}
	
	protected fun storeIntern(key: String, value: String, encrypted: Boolean = false): Config {
		val entry = getToken(key)
		entry.ckey = key
		entry.value = value
		entry.encrypted = encrypted
		entityManager.persist(entry)
		return entry
	}
	
}
