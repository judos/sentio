package ch.judos.sentio.entities


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(name = "monitor_data")
class MonitorData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int? = null
	
	@ManyToOne
	lateinit var website: Website
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false, columnDefinition = "Date(0)")
	lateinit var date: LocalDate
	
	@Column(nullable = false, columnDefinition = "Time(0)")
	lateinit var firstCheck: LocalTime
	
	@Column(nullable = false, columnDefinition = "DateTime(0)")
	lateinit var lastCheck: LocalDateTime
	
	@Column(nullable = false)
	var succeeded: Int = 0
	
	@Column(nullable = false)
	var failed: Int = 0
	
}
