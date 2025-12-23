package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity(name = "config")
open class Config {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@Column(nullable = false, unique = true)
	lateinit var ckey: String
	
	@Column(nullable = false)
	lateinit var value: String
	
	@Column(nullable = false)
	var encrypted: Boolean = false
	
}
