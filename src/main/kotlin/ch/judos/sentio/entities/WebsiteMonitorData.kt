package ch.judos.sentio.entities


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "website_monitor_data")
class WebsiteMonitorData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int? = null
	
	@ManyToOne
	lateinit var website: Website
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false)
	lateinit var datetime: LocalDateTime
	
	@Column(nullable = false)
	var success: Boolean = false
	
	@Column(nullable = false)
	var value: Long = 0L
	
	@Column(nullable = true)
	var message: String? = null
	
}
