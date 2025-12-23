package ch.judos.sentio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity(name = "channel")
class Channel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	lateinit var name: String
	
	lateinit var type: String
	
	/** encrypted json data */
	@Column(length = 1024)
	lateinit var credentials: String
	
	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "VARCHAR(19)")
	lateinit var created: LocalDateTime
	
	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "VARCHAR(19)")
	lateinit var lastUsed: LocalDateTime
	
}
