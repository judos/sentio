package ch.judos.sentio

import ch.judos.sentio.entities.Config
import ch.judos.sentio.entities.QConfig
import ch.judos.sentio.entities.QUser
import ch.judos.sentio.entities.User
import ch.judos.sentio.services.PasswordService
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.util.concurrent.Executors
import javax.sql.DataSource


@ApplicationScoped
class Sentio(
		val query: JPAQueryFactory,
		val dataSource: DataSource,
		val entityManager: EntityManager
) {
	
	companion object {
		val pool = Executors.newCachedThreadPool()
	}
	
	val qUser = QUser.user
	val qConfig = QConfig.config
	
	@Transactional
	fun onStartup(@Observes event: StartupEvent) {
		val javaVersion = System.getProperty("java.version")
		val kotlinVersion = KotlinVersion.CURRENT
		Log.info("Kotlin: $kotlinVersion Java: $javaVersion")
		
		val configs = query.from(qConfig).fetchCount()
		if (configs == 0L) {
			Config.defaults.forEach {
				entityManager.persist(it)
			}
		}
		val users = query.from(qUser).fetchCount()
		if (users == 0L) {
			val hash = PasswordService.createSaltAndHash("1234")
			entityManager.persist(User("admin", hash))
			Log.warn("Created 'admin' with pw '1234' as default user.")
		}
		
	}
}
