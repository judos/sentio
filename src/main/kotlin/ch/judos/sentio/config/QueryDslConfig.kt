package ch.judos.sentio.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.persistence.EntityManager

@ApplicationScoped
class QueryDslConfig {
	
	@Produces
	fun queryFactory(entityManager: EntityManager): JPAQueryFactory {
		return JPAQueryFactory(entityManager)
	}
}

