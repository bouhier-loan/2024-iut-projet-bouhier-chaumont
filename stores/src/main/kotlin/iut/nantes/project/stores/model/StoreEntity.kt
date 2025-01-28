package iut.nantes.project.stores.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size

@Entity
@Table(name = "stores")
class StoreEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    @Size(min = 3, max = 30)
    var name: String,

    @ManyToOne
    @JoinColumn(name = "contact_id")
    var contact: ContactEntity,

    @OneToMany(cascade = [CascadeType.ALL])
    var products: List<ProductEntity> = emptyList()
) {
    constructor() : this(null, "", ContactEntity())
}