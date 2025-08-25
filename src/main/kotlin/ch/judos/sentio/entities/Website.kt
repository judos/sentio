package ch.judos.sentio.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDateTime

@Entity
class Website : PanacheEntity() {
	companion object : PanacheCompanion<Website>
	
	@Column(nullable = false, unique = true)
	lateinit var name: String
	
	@Column(nullable = false)
	lateinit var url: String
	
	@Column(nullable = true)
	var rCheckEveryMin: Int? = 5
	
	@Column(nullable = true)
	var rLastCheck: LocalDateTime? = null
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	var rStatus: WebsiteStatus = WebsiteStatus.INACTIVE
	
	@Column(nullable = true)
	var rAlertIfUnreachableForMin: Int? = 15
	
	@Column(nullable = true)
	var rComment: String? = null
	
	
	@Column(nullable = true)
	var sslCheckExpiryEveryMin: Int? = 60 * 24
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	var sslStatus: SslStatus = SslStatus.INACTIVE
	
	@Column(nullable = true)
	var sslLastExpiryCheck: LocalDateTime? = null
	
	@Column(nullable = true)
	var sslAlertIfExpiryBelowMin: Int? = 60 * 24 * 7 // 1 Woche
	
	@Column(nullable = true)
	var sslComment: String? = null
}
