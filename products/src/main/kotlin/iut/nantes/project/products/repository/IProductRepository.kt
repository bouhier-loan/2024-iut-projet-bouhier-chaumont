package iut.nantes.project.products.repository


import iut.nantes.project.products.dto.ProductDTO
import java.util.*

interface IProductRepository {
    fun findAll(): List<ProductDTO>
    fun findById(id: UUID): ProductDTO?
    fun save(productDTO: ProductDTO) : ProductDTO?
    fun update(productId : UUID, productDTO: ProductDTO) : ProductDTO?
    fun deleteById(id: UUID): Boolean
}