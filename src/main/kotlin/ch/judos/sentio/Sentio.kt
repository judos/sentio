package ch.judos.sentio

import ch.judos.sentio.entities.QConfig
import ch.judos.sentio.entities.QUser
import ch.judos.sentio.services.PasswordService
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import java.nio.file.Files
import java.nio.file.Paths
import javax.sql.DataSource


@ApplicationScoped
class Sentio(
	val query: JPAQueryFactory,
	val dataSource: DataSource
) {
	
	val qUser = QUser.user
	val qConfig = QConfig.config
	
	fun onStartup(@Observes event: StartupEvent) {
		val javaVersion = System.getProperty("java.version")
		val kotlinVersion = KotlinVersion.CURRENT
		Log.info("Kotlin: $kotlinVersion Java: $javaVersion")
		
		// run sql file script
		val configs = query.from(qConfig).fetchCount()
		if (configs == 0L) {
			val sql = Files.readString(Paths.get(javaClass.classLoader.getResource("db/post_startup.sql")!!.toURI()))
			dataSource.connection.use { conn ->
				conn.autoCommit = true
				conn.createStatement().use { stmt ->
					sql.split(";")
						.map { it.trim() }
						.filter { it.isNotEmpty() }
						.forEach { sqlCommand ->
							@Suppress("SqlSourceToSinkFlow")
							stmt.execute(sqlCommand)
						}
				}
			}
		}
		
		val users = query.from(qUser).fetchCount()
		if (users == 0L) {
			val hash = PasswordService.createSaltAndHash("1234")
			Log.warn(
				"Create an example user 'admin' with pw '1234':\n" +
						"INSERT INTO user (username, password) VALUES ('admin', '$hash');"
			)
		}
		
	}
}
