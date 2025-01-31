package iut.nantes.project.products.service

import iut.nantes.project.products.dto.FamilyDTO
import iut.nantes.project.products.repository.IFamilyRepository
import java.util.*

class FamilyService(private val repository: IFamilyRepository) {

    fun addFamily(name: String, description: String) : FamilyDTO? {
        var uuid = UUID.randomUUID()
        while(findById(uuid) != null) {
            uuid = UUID.randomUUID()
        }
        return repository.save(FamilyDTO(uuid, name, description))
    }

    fun findAll(): List<FamilyDTO> = repository.findAll();

    fun findById(id: UUID): FamilyDTO? = repository.findById(id)

    fun updateFamily(id: UUID, familyDTO: FamilyDTO): FamilyDTO? {
        if(findById(id) == null) return null
        return repository.update(id, familyDTO)
    }

    fun deleteById(id: UUID) : Boolean = repository.deleteById(id)

}