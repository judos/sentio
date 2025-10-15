package ch.judos.sentio.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
open class Config {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@Column(nullable = false)
	lateinit var ckey: String
	
	@Column(nullable = false)
	lateinit var value: String
	
}
