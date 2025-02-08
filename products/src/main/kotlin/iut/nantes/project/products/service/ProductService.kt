package iut.nantes.project.products.service


import iut.nantes.project.products.dto.ProductDTO
import iut.nantes.project.products.dto.StoreResponseDTO
import iut.nantes.project.products.repository.IProductRepository
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

class ProductService(private val repository: IProductRepository, private val familyService: FamilyService, private val webClient: WebClient) {

    fun create(productDTO: ProductDTO) : ProductDTO? {
        var uuid = UUID.randomUUID()
        while(findById(uuid) != null) {
            uuid = UUID.randomUUID()
        }
        val family = familyService.findById(productDTO.family.id!!) ?: return null
        return repository.save(ProductDTO(uuid,productDTO.name,productDTO.description, productDTO.price, family))
    }

    fun findAll(familyName : String? = null, minPrice: Int? = null, maxPrice : Int? = null) : List<ProductDTO> {
        var products = repository.findAll()
        if(familyName != null) {
            products = products.filter { it.family.name == familyName }
        }
        if(minPrice != null) {
            products = products.filter { it.price.amount >= minPrice }
        }
        if(maxPrice != null) {
            products = products.filter { it.price.amount <= maxPrice }
        }
        return products
    }

    fun findById(id: UUID) : ProductDTO? = repository.findById(id)

    fun findAllByFamilyId(familyId : UUID) : List<ProductDTO> {
        return repository
            .findAll()
            .filter { it.family.id == familyId }
    }

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

    fun canDeleteProduct(productId: UUID) : Boolean {
        val result = webClient.get().uri("/api/v1/stores/products/${productId}").retrieve().bodyToMono(StoreResponseDTO::class.java).block()
            ?: throw IllegalArgumentException("Stores can't be join")

        return result.totalQuantity == 0
    }

}