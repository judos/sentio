package ch.judos.sentio.entities


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "monitor_error")
class MonitorError {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@ManyToOne(optional = false)
	lateinit var monitored: Monitored
	
	@Column(nullable = false, columnDefinition = "DateTime(0)")
	lateinit var dateTime: LocalDateTime
	
	@Column(nullable = false)
	lateinit var message: String
	
}
