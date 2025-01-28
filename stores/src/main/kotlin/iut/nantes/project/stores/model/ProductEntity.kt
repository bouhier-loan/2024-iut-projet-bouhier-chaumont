package iut.nantes.project.stores.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    var id: String,
    val name: String,
    var quantity: Int
) {
    constructor() : this("", "", 0)
}