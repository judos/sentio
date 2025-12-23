package ch.judos.sentio.entities

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity(name = "monitored")
open class Monitored {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long? = null
	
	@Column(nullable = false)
	lateinit var name: String
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json", nullable = false)
	lateinit var settings: Map<String, String>
	
	@Column(nullable = false)
	lateinit var monitor: String
	
	@Column(nullable = false)
	var checkEveryMin: Int = 5
	
	@Column(nullable = false)
	var alertIfFailingForMin: Int = 15
	
	@OneToMany(mappedBy = "monitored", cascade = [CascadeType.ALL], orphanRemoval = true)
	lateinit var data: MutableList<Data>
	
	
	fun updateFrom(monitored: Monitored) {
		this.name = monitored.name
		this.settings = monitored.settings
		this.checkEveryMin = monitored.checkEveryMin
		this.alertIfFailingForMin = monitored.alertIfFailingForMin
	}
	
}
