package iut.nantes.project.products.controller

import iut.nantes.project.products.dto.FamilyDTO
import iut.nantes.project.products.service.FamilyService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.UUID

@RestController
class FamilyController(private val service: FamilyService) {

    @GetMapping("/api/v1/families")
    fun getFamilies() = ResponseEntity.ok(service.findAll())

    @GetMapping("/api/v1/families/{id}")
    fun getFamilyById(@PathVariable id: String): ResponseEntity<FamilyDTO> {
        try {
            UUID.fromString(id);
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
        return service.findById(UUID.fromString(id))?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/api/v1/families")
    fun createFamily(@Valid @RequestBody familyDTO: FamilyDTO): ResponseEntity<FamilyDTO> {
        return try {
            val result = service.addFamily(familyDTO.name, familyDTO.description)
            if (result != null) {
                ResponseEntity.created(URI.create("/api/v1/products/${result.id}")).body(result)
            } else ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(409).build()
        }

    }

    @PutMapping("/api/v1/families/{id}")
    fun updateFamily(@PathVariable id: String, @RequestBody familyDTO: FamilyDTO): ResponseEntity<FamilyDTO> {
        try {
            UUID.fromString(id);
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            val result = service.updateFamily(UUID.fromString(id),familyDTO)
            if (result != null) {
                ResponseEntity.ok(result)
            } else ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(409).build()
        }
    }

    @DeleteMapping("/api/v1/families/{id}")
    fun deleteFamily(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            if(service.deleteById(UUID.fromString(id))) ResponseEntity.ok().build()
            else ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}