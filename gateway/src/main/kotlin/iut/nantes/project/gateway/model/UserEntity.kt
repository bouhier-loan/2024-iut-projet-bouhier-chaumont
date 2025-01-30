package iut.nantes.project.gateway.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    val login: String,
    var password: String,
    val isAdmin: Boolean
) {
    constructor() : this("", "", false)
}