package ch.judos.sentio.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class Website : PanacheEntity() {
    companion object : PanacheCompanion<Website>

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(nullable = false)
    lateinit var url: String

    var lastSuccess: LocalDateTime? = null
}

