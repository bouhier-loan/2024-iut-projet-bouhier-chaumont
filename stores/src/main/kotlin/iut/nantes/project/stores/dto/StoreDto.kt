package iut.nantes.project.stores.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class StoreDto(
    var id: Long? = null,

    @field:Size(min = 3, max = 30)
    val name: String,

    @field:NotNull
    val contact: ContactDto,

    @field:NotNull
    val products: List<ProductDto> = emptyList(),
)