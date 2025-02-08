package iut.nantes.project.stores.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class ContactDto(
    val id: Long?,

    @field:Email
    val email: String,

    @field:Pattern(regexp = "^\\d{10}$")
    val phone: String,

    @field:Valid
    val address: AddressDto
)
