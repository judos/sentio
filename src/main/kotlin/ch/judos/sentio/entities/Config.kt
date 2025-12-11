package ch.judos.sentio.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

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
