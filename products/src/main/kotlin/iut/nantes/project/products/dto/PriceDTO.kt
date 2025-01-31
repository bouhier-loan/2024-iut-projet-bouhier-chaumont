package iut.nantes.project.products.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class PriceDTO(
    @field:Min(value = 0)
    val amount: Int,
    @field:Pattern(regexp = "^[A-Z]{3}$")
    val currency: String,
)