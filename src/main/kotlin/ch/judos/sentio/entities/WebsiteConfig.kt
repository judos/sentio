package ch.judos.sentio.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity(name = "website_config")
open class WebsiteConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int? = null
	
	@ManyToOne
	lateinit var website: Website
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false)
	var checkEveryMin: Int = 5
	
	@Column(nullable = false)
	var alertIfUnreachableForMin: Int = 15
	
}
