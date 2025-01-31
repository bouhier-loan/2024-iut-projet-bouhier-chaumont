package iut.nantes.project.products.entity

import iut.nantes.project.products.dto.FamilyDTO
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class FamilyEntity(
    @Id val id: UUID? = null,
    @Column(unique = true) val name: String,
    val description: String
) {
    constructor() : this(UUID.randomUUID(), "", "")

    fun toFamilyDTO() : FamilyDTO = FamilyDTO(id, name, description)
}