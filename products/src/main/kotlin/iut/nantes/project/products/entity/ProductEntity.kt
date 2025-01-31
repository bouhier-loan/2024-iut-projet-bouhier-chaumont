package iut.nantes.project.products.entity

import jakarta.persistence.*
import java.util.*

@Entity
data class ProductEntity(
    @Id @Column(name = "product_id")
    val id: UUID? = null,
    val name: String,
    val description: String? = null,
    val amount: Int,
    val currency: String,
    @ManyToOne(cascade = [CascadeType.ALL])
    val familyEntity: FamilyEntity

) {
    constructor() : this(id = null, name = "", description = null, amount = 0, currency = "", FamilyEntity())
}