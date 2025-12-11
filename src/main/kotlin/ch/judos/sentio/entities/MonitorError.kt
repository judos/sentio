package ch.judos.sentio.entities


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "monitor_error")
class MonitorError {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int? = null
	
	@ManyToOne
	lateinit var website: Website
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false, columnDefinition = "DateTime(0)")
	lateinit var dateTime: LocalDateTime
	
	@Column(nullable = false)
	lateinit var message: String
	
}
