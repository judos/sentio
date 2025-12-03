package ch.judos.sentio.entities

import jakarta.persistence.*

@Entity
class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = 0
	
	@Column(nullable = false, unique = true)
	lateinit var username: String
	
	@Column(nullable = false)
	lateinit var password: String
}
