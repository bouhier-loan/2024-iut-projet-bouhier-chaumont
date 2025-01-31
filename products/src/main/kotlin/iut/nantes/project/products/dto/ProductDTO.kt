package iut.nantes.project.products.dto
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class ProductDTO (
    val id: UUID? = null,
    @field:Size(min = 2, max = 20, message = "name length must be in range 2..20")
    val name: String,
    @field:Size(min = 5, max = 100, message = "Description length must be in range 5..100")
    val description: String? = null,
    @field:Valid
    val price: PriceDTO,
    val family: FamilyDTO
)
