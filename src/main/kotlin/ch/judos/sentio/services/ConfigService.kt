package ch.judos.sentio.services

import ch.judos.sentio.entities.QConfig
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@ApplicationScoped
class ConfigService(
		var query: JPAQueryFactory,
		@PersistenceContext
		var entityManager: EntityManager
) {
	
	val qConfig = QConfig.config
	
	fun getStr(key: String): String {
		val config = query.selectFrom(qConfig).where(qConfig.ckey.eq(key)).fetchOne()
			?: throw RuntimeException("Config with key '$key' not found")
		return config.value
	}
	
	fun getFloat(key: String): Float {
		return getStr(key).toFloat()
	}
	
	
}
