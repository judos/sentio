package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity(name = "config")
open class Config() {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@Column(nullable = false, unique = true)
	lateinit var ckey: String
	
	@Column(nullable = false)
	lateinit var value: String
	
	@Column(nullable = false)
	var encrypted: Boolean = false
	
	constructor(ckey: String, value: String, encrypted: Boolean) : this() {
		this.ckey = ckey
		this.value = value
		this.encrypted = encrypted
	}
	
	companion object {
		
		val defaults = listOf(
			Config("image_format", "webp", false),
			Config("image_quality", "0.85", false),
		)
		
	}
}
