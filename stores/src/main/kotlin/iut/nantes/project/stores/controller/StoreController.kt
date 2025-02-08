package iut.nantes.project.stores.controller

import iut.nantes.project.stores.dto.ProductDto
import iut.nantes.project.stores.dto.ProductOverviewDto
import iut.nantes.project.stores.dto.StoreDto
import iut.nantes.project.stores.service.StoreService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
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
class StoreController(
    val storeService: StoreService
) {
    @PostMapping("/api/v1/stores")
    fun createStore(
        @Valid @RequestBody store: StoreDto
    ): ResponseEntity<StoreDto> {
        store.id = null
        val withId = storeService.saveStore(store)
        return ResponseEntity.status(HttpStatus.CREATED).body(withId)
    }

    @GetMapping("/api/v1/stores")
    fun getStores(): ResponseEntity<List<StoreDto>> {
        val result = storeService.findAllStores().sortedBy { it.name }
        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/v1/stores/{id}")
    fun getStore(
        @PathVariable id: Long
    ): ResponseEntity<StoreDto> {
        return storeService.findStoreById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/api/v1/stores/{id}")
    fun modifyStore(
        @Valid @RequestBody store: StoreDto,
        @PathVariable id: Long
    ): ResponseEntity<StoreDto> {
        val withId = storeService.modifyStore(id, store)
        return ResponseEntity.ok(withId)
    }

    @DeleteMapping("/api/v1/stores/{id}")
    fun deleteStore(
        @PathVariable id: Long
    ): ResponseEntity<Unit> {
        if (storeService.findStoreById(id) == null) {
            return ResponseEntity.notFound().build()
        }
        storeService.deleteStoreById(id)
        return ResponseEntity.status(204).build()
    }

    @PostMapping("/api/v1/stores/{id}/products/{productId}/add")
    fun addProductToStore(
        @PathVariable id: Long,
        @PathVariable productId: String,
        @RequestParam(defaultValue = "1") @Min(1) quantity: Int
    ): ResponseEntity<ProductDto> {
        if (storeService.findStoreById(id) == null) {
            return ResponseEntity.notFound().build()
        }

        val withId = storeService.addProductToStore(id, ProductDto(productId, "", quantity))
        return ResponseEntity.status(HttpStatus.CREATED).body(withId)
    }

    @DeleteMapping("/api/v1/stores/{id}/products/{productId}/remove")
    fun removeProductFromStore(
        @PathVariable id: Long,
        @PathVariable productId: String,
        @RequestParam(defaultValue = "1") @Min(1) quantity: Int
    ): ResponseEntity<ProductDto> {
        println("quantity: $quantity")
        if (storeService.findStoreById(id) == null) {
            return ResponseEntity.notFound().build()
        }
        val withId = storeService.removeProductFromStore(id, productId, quantity)

        println(withId)

        return when {
            withId.quantity == 0 -> ResponseEntity.status(409).build()
            else -> ResponseEntity.ok(withId)
        }
    }

    @DeleteMapping("/api/v1/stores/{id}/products/")
    fun deleteProductFromStore(
        @PathVariable id: Long,
        @RequestBody products: List<String>
    ): ResponseEntity<Unit> {
        // Check if the store exists
        if (storeService.findStoreById(id) == null) {
            return ResponseEntity.notFound().build()
        }

        // Check if the products list have twice the same product
        if (products.size != products.distinct().size) {
            return ResponseEntity.badRequest().build()
        }

        products.forEach { product ->
            storeService.deleteProductFromStore(id, product)
        }

        return ResponseEntity.noContent().build()
    }

    /* ---- */

    @GetMapping("/api/v1/stores/products/{productId}")
    fun getProductOverview(
        @PathVariable productId: String
    ): ResponseEntity<ProductOverviewDto> {
        val result = storeService.findProductOverview(productId)
        return ResponseEntity.ok(result)
    }
}