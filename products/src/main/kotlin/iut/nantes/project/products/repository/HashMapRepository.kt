package iut.nantes.project.products.repository

import iut.nantes.project.products.dto.FamilyDTO
import iut.nantes.project.products.dto.ProductDTO
import java.util.*

class HashMapFamilyRepository : IFamilyRepository {
    private val families = hashMapOf<UUID, FamilyDTO>()

    override fun findAll(): List<FamilyDTO> = families.values.toList()

    override fun findById(id: UUID) = families[id]

    override fun save(familyDTO: FamilyDTO) : FamilyDTO? {
        if(families.values.any { familyDTO.name == it.name }) throw IllegalArgumentException("Name already exists")
        familyDTO.id?.let { families[it] = familyDTO }
        return families[familyDTO.id]
    }

    override fun update(id: UUID, familyDTO: FamilyDTO): FamilyDTO? {
        if(families.values.any { familyDTO.name == it.name }) throw IllegalArgumentException("Name already exists")
        families[id] = familyDTO
        return families[familyDTO.id]
    }

    override fun deleteById(id: UUID): Boolean {
        if(findById(id) == null) return false
        families.remove(id); return true
    }
}


class HashMapProductRepository : IProductRepository {
    private val products = hashMapOf<UUID, ProductDTO>()

    override fun findAll(): List<ProductDTO> = products.values.toList()

    override fun findById(id: UUID) = products[id]

    override fun save(productDTO: ProductDTO) : ProductDTO? {
        productDTO.id?.let { products[it] = productDTO }
        return products[productDTO.id]
    }

    override fun update(productId : UUID, productDTO: ProductDTO) : ProductDTO? {
        if(products[productId] == null) return null
        products[productId] = productDTO
        return products[productId]
    }

    override fun deleteById(id: UUID) : Boolean {
        if(findById(id) == null) return false
        products.remove(id); return true
    }

}