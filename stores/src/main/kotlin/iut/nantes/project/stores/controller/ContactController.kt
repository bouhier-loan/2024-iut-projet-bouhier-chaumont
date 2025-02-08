package iut.nantes.project.stores.controller

import iut.nantes.project.stores.dto.ContactDto
import iut.nantes.project.stores.service.ContactService
import iut.nantes.project.stores.service.StoreService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ContactController(
    val contactService: ContactService,
    val storeService: StoreService
) {
    @PostMapping("/api/v1/contacts")
    fun createContact(@Valid @RequestBody contact: ContactDto): ResponseEntity<ContactDto> {
     val withId = contactService.saveContact(contact)
     return ResponseEntity.status(HttpStatus.CREATED).body(withId)
    }

    @GetMapping("/api/v1/contacts")
    fun getContacts(@RequestParam city: String?): ResponseEntity<List<ContactDto>> {
        var result = contactService.findAllContacts()

        // Filter by city
        city?.let {
            result = result.filter { it.address.city == city }
        }

        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/v1/contacts/{id}")
    fun getContact(@PathVariable id: Long): ResponseEntity<ContactDto> {
        return contactService.findContactById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/api/v1/contacts/{id}")
    fun updateContact(@Valid @RequestBody contact: ContactDto, @PathVariable id: Long): ResponseEntity<ContactDto> {
        val existingContact = contactService.findContactById(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        if (contact.email != existingContact.email && contact.phone != existingContact.phone) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        val updatedContact = contactService.updateContact(id, contact)
        return ResponseEntity.ok(updatedContact)
    }

    @DeleteMapping("/api/v1/contacts/{id}")
    fun deleteContact(@PathVariable id: Long): ResponseEntity<Unit> {
        if (contactService.findContactById(id) == null) {
            return ResponseEntity.notFound().build()
        }

        // Check if the contact is associated with a store
        val stores = storeService.findAllStores()
        if (stores.any { it.contact.id == id }) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        contactService.deleteContactById(id)
        return ResponseEntity.status(204).build()
    }
}