package iut.nantes.project.products.controller

import iut.nantes.project.products.dto.ProductDTO
import iut.nantes.project.products.service.ProductService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ProductController(val productService: ProductService) {

    @PostMapping("/api/v1/products")
    fun createProduct(@Valid @RequestBody productDTO: ProductDTO) : ResponseEntity<ProductDTO> {
        if(productDTO.family.id == null) return ResponseEntity.badRequest().build()
        val product = productService.create(productDTO) ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.status(HttpStatus.CREATED).body(product)
    }

    @GetMapping("/api/v1/products")
    fun getProducts(@RequestParam familyname : String?,
                    @RequestParam @Min(1) minprice : Int?,
                    @RequestParam @Min(1) maxprice : Int?) : ResponseEntity<List<ProductDTO>> {
        if (minprice != null && maxprice != null) {
            if(minprice >= maxprice) return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(productService.findAll(familyname, minprice, maxprice))
    }

    @GetMapping("/api/v1/products/{id}")
    fun getProduct(@PathVariable id : String) : ResponseEntity<ProductDTO> {
        try {
            val product = productService.findById(UUID.fromString(id)) ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(product)
        } catch (ex: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/api/v1/products/{id}")
    fun updateProduct(@PathVariable id : String, @Valid @RequestBody productDTO: ProductDTO) : ResponseEntity<ProductDTO> {
        return try {
            val result = productService.update(UUID.fromString(id),productDTO)
            println(result)
            if (result != null) {
                ResponseEntity.ok(result)
            } else ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/api/v1/products/{id}")
    fun deleteProduct(@PathVariable id : String) : ResponseEntity<Void> {
        try {
            val result = productService.delete(UUID.fromString(id))
            return if(result) ResponseEntity.ok().build()
            else ResponseEntity.badRequest().build()
        } catch (ex: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
    }
}