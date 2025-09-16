package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity
open class Website {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int? = null
	
	@Column(nullable = false, unique = true)
	lateinit var name: String
	
	@Column(nullable = false)
	lateinit var url: String
	
	@OneToMany(mappedBy = "website", cascade = [CascadeType.ALL], orphanRemoval = true)
	var configs: MutableList<WebsiteConfig> = mutableListOf()
	
	@OneToMany(mappedBy = "website", cascade = [CascadeType.ALL], orphanRemoval = true)
	var data: MutableList<WebsiteMonitorData> = mutableListOf()
	
}
