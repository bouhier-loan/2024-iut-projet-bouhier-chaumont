package iut.nantes.project.products.repository

import iut.nantes.project.products.entity.FamilyEntity
import iut.nantes.project.products.entity.ProductEntity
import iut.nantes.project.products.dto.FamilyDTO
import iut.nantes.project.products.dto.PriceDTO
import iut.nantes.project.products.dto.ProductDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.*


interface FamilyJpaRepository : JpaRepository<FamilyEntity, UUID>

interface ProductJpaRepository : JpaRepository<ProductEntity, UUID>

class H2FamilyRepository(private val jpaRepository: FamilyJpaRepository) : IFamilyRepository {
    override fun findAll(): List<FamilyDTO> = jpaRepository.findAll().map { it.toFamilyDTO() }

    override fun findById(id: UUID): FamilyDTO? = jpaRepository.findByIdOrNull(id)?.toFamilyDTO()

    override fun save(familyDTO: FamilyDTO) : FamilyDTO = jpaRepository.save(familyDTO.toFamilyEntity()).toFamilyDTO()

    override fun update(id: UUID, familyDTO: FamilyDTO): FamilyDTO {
        return jpaRepository.save(FamilyDTO(id,familyDTO.name, familyDTO.description).toFamilyEntity()).toFamilyDTO()
    }

    override fun deleteById(id: UUID) : Boolean {
        if(findById(id) == null) return false
        else jpaRepository.deleteById(id); return true
    }
}

class H2ProductRepository(private val jpaRepository: ProductJpaRepository) : IProductRepository {
    override fun findAll(): List<ProductDTO> = jpaRepository.findAll().map { it.toProductDTO() }

    override fun findById(id: UUID): ProductDTO? = jpaRepository.findById(id).orElse(null)?.toProductDTO()

    override fun save(productDTO: ProductDTO): ProductDTO {
        return jpaRepository.save(productDTO.toProductEntity()).toProductDTO()
    }

    override fun update(productId : UUID, productDTO: ProductDTO) : ProductDTO {
        return jpaRepository.save(productDTO.toProductEntity()).toProductDTO()
    }

    override fun deleteById(id: UUID) : Boolean {
        if(findById(id) == null) return false
        else jpaRepository.deleteById(id); return true
    }

    private fun ProductDTO.toProductEntity(): ProductEntity =
        ProductEntity(id, name, description, price.amount, price.currency, family.toFamilyEntity())

    private fun ProductEntity.toProductDTO(): ProductDTO = ProductDTO(id, name, description, PriceDTO(amount, currency), familyEntity.toFamilyDTO())

}