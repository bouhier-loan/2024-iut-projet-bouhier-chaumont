package iut.nantes.project.products.dto

import iut.nantes.project.products.entity.FamilyEntity
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class FamilyDTO (
    val id: UUID? = null,
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 3, max = 30, message = "name length must be in range 3..30")
    val name: String,
    @field:Size(min = 5, max = 100, message = "Description length must be in range 5..100")
    val description: String,
) {
    fun toFamilyEntity() : FamilyEntity = FamilyEntity(id, name, description)
}