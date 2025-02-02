package iut.nantes.project.stores.service

import iut.nantes.project.stores.dto.AddressDto
import iut.nantes.project.stores.dto.ContactDto
import iut.nantes.project.stores.model.AddressEntity
import iut.nantes.project.stores.model.ContactEntity
import iut.nantes.project.stores.repository.ContactRepository
import org.springframework.stereotype.Service

fun ContactEntity.toDto() = ContactDto(id, email, phone, address.toDto())
private fun AddressEntity.toDto() = AddressDto(street, city, postalCode)
fun ContactDto.toEntity() : ContactEntity {
    val address = AddressEntity(null, this.address.street, this.address.city, this.address.postalCode)
    val contact = ContactEntity(id, email, phone, address)
    return contact
}

@Service
class ContactService(
    private val contactRepository: ContactRepository,
) {
    fun saveContact(contactDto: ContactDto): ContactDto {
        val contact = contactDto.toEntity()
        val result = contactRepository.save<ContactEntity>(contact)
        return result.toDto()
    }

    fun findContactById(id: Long): ContactDto? {
        return contactRepository.findById(id).map { it.toDto() }.orElse(null)
    }

    fun findAllContacts(): List<ContactDto> {
        return contactRepository.findAll().map { it.toDto() }
    }

    fun deleteContactById(id: Long) {
        contactRepository.deleteById(id)
    }

    fun updateContact(id: Long, contactDto: ContactDto): ContactDto {
        val contact = contactDto.toEntity()
        val result = contactRepository.save(contact)
        return result.toDto()
    }
}