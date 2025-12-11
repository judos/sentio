package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity(name = "website")
open class Website {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JvmField
	var id: Long? = null
	
	var _id: Long?
		@JvmName("getId")
		get() = id
		@JvmName("setId")
		set(value) {
			id = value
		}
	
	@Column(nullable = false, unique = true)
	lateinit var name: String
	
	@Column(nullable = false)
	lateinit var url: String
	
	@OneToMany(mappedBy = "website", cascade = [CascadeType.ALL], orphanRemoval = true)
	var configs: MutableList<WebsiteConfig> = mutableListOf()
	
	@OneToMany(mappedBy = "website", cascade = [CascadeType.ALL], orphanRemoval = true)
	var data: MutableList<MonitorData> = mutableListOf()
	
}
