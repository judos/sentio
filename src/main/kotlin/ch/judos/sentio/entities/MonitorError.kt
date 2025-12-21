package ch.judos.sentio.entities


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "monitor_error")
class MonitorError {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@ManyToOne
	lateinit var website: Website
	
	@ManyToOne(optional = false)
	lateinit var config: WebsiteConfig
	
	@Column(nullable = false, columnDefinition = "DateTime(0)")
	lateinit var dateTime: LocalDateTime
	
	@Column(nullable = false)
	lateinit var message: String
	
}
