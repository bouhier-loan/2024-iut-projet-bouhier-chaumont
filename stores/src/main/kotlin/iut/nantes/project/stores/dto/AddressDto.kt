package iut.nantes.project.stores.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AddressDto(
    @field:Size(min = 5, max = 50)
    val street: String,

    @field:Size(min = 1, max = 30)
    val city: String,

    @field:Pattern(regexp = "^\\d{5}$")
    val postalCode: String
)