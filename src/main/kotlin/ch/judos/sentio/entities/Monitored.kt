package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity(name = "monitored")
open class Monitored {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@Column(nullable = false, unique = true)
	lateinit var name: String
	
	@Column(nullable = false, length = 65535)
	lateinit var settings: String
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false)
	var checkEveryMin: Int = 5
	
	@Column(nullable = false)
	var alertIfFailingForMin: Int = 15
	
	@OneToMany(mappedBy = "monitored", cascade = [CascadeType.ALL], orphanRemoval = true)
	var data: MutableList<Data> = mutableListOf()
	
	
	fun updateFrom(monitored: Monitored) {
		this.checkEveryMin = monitored.checkEveryMin
		this.alertIfFailingForMin = monitored.alertIfFailingForMin
	}
	
}
