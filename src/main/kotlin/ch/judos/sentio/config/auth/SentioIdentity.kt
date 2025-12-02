package ch.judos.sentio.config.auth

import jakarta.enterprise.context.RequestScoped

@RequestScoped
class SentioIdentity {
    var userId: Long? = null
    var username: String? = null
}

