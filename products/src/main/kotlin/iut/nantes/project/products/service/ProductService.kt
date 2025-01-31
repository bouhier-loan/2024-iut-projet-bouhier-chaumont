package iut.nantes.project.products.service


import iut.nantes.project.products.dto.ProductDTO
import iut.nantes.project.products.repository.IProductRepository
import java.util.*

class ProductService(private val repository: IProductRepository, private val familyService: FamilyService) {

    fun create(productDTO: ProductDTO) : ProductDTO? {
        var uuid = UUID.randomUUID()
        while(findById(uuid) != null) {
            uuid = UUID.randomUUID()
        }
        val family = familyService.findById(productDTO.family.id!!) ?: return null
        return repository.save(ProductDTO(uuid,productDTO.name,productDTO.description, productDTO.price, family))
    }

    fun findAll() : List<ProductDTO> = repository.findAll()

    fun findById(id: UUID) : ProductDTO? = repository.findById(id)

    fun update(productId: UUID, productDTO: ProductDTO) : ProductDTO? {
        if(findById(productId) == null) return null
        if(productDTO.family.id == null) return null
        val family = familyService.findById(productDTO.family.id) ?: return null
        return repository.update(productId, ProductDTO(productId, productDTO.name, productDTO.description, productDTO.price, family))
    }

    fun delete(id: UUID) : Boolean {
        findById(id) ?: return false
        repository.deleteById(id)
        return true
    }

}