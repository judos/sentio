package ch.judos.sentio.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.persistence.EntityManager

@ApplicationScoped
class QueryDslConfig {
	
	init {
		println("Using java v: ${System.getProperty("java.version")}")
	}
	
	@Produces
	fun queryFactory(entityManager: EntityManager): JPAQueryFactory {
		return JPAQueryFactory(entityManager)
	}
}

