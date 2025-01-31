package iut.nantes.project.products.repository

import iut.nantes.project.products.dto.FamilyDTO
import java.util.*

interface IFamilyRepository {
    fun findAll(): List<FamilyDTO>
    fun findById(id: UUID): FamilyDTO?
    fun save(familyDTO: FamilyDTO) : FamilyDTO?
    fun update(id: UUID, familyDTO: FamilyDTO) : FamilyDTO?
    fun deleteById(id: UUID) : Boolean
}