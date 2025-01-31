package iut.nantes.project.stores.service

import iut.nantes.project.stores.dto.AddressDto
import iut.nantes.project.stores.dto.ContactDto
import iut.nantes.project.stores.dto.ProductDto
import iut.nantes.project.stores.dto.ProductOverviewDto
import iut.nantes.project.stores.dto.StoreDto
import iut.nantes.project.stores.dto.StoreProductOverviewDto
import iut.nantes.project.stores.model.AddressEntity
import iut.nantes.project.stores.model.ContactEntity
import iut.nantes.project.stores.model.ProductEntity
import iut.nantes.project.stores.model.StoreEntity
import iut.nantes.project.stores.repository.ContactRepository
import iut.nantes.project.stores.repository.ProductRepository
import iut.nantes.project.stores.repository.StoreRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

private fun ContactEntity.toDto() = ContactDto(id, email, phone, address.toDto())
private fun AddressEntity.toDto() = AddressDto(street, city, postalCode)
private fun ProductEntity.toDto() = ProductDto(id, name, quantity)
private fun StoreEntity.toDto() = StoreDto(id, name, contact.toDto(), products.map { it.toDto() })

private fun ContactDto.toEntity() : ContactEntity {
    val address = AddressEntity(null, this.address.street, this.address.city, this.address.postalCode)
    val contact = ContactEntity(id, email, phone, address)
    return contact
}

private fun StoreDto.toEntity() : StoreEntity {
    val products = products.map { ProductEntity(it.id, it.name, it.quantity) }
    val contact = contact.toEntity()
    val store = StoreEntity(id, name, contact, products)
    return store
}

@Service
class DatabaseProxy(
    private val contactRepository: ContactRepository,
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository,
    private val webClient: WebClient
) {
    fun saveContact(contactDto: ContactDto): ContactDto {
        val contact = contactDto.toEntity()
        var result = contactRepository.save<ContactEntity>(contact)
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
        var result = contactRepository.save(contact)
        return result.toDto()
    }

    fun saveStore(storeDto: StoreDto): StoreDto {
        println(storeDto)
        // If the contact doesnt exits, create it in the database
        if (storeDto.contact.id == null || findContactById(storeDto.contact.id) == null) {
            saveContact(storeDto.contact).toEntity()
        }

        val store = storeDto.toEntity()

        // We ignore the product list
        store.products = emptyList()

        var result = storeRepository.save<StoreEntity>(store)

        return result.toDto()
    }

    fun findStoreById(id: Long): StoreDto? {
        return storeRepository.findById(id).map { it.toDto() }.orElse(null)
    }

    fun findAllStores(): List<StoreDto> {
        return storeRepository.findAll().map { it.toDto() }
    }

    fun modifyStore(id: Long, storeDto: StoreDto): StoreDto {
        // Retrieve the store entity from the database
        var store: StoreEntity = storeRepository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Store not found")

        // Retrieve the contact entity from the database
        var contact: ContactEntity =
        if (storeDto.contact.id == null || findContactById(storeDto.contact.id) == null) {
            saveContact(storeDto.contact).toEntity()
        } else {
            findContactById(storeDto.contact.id)?.toEntity()!!
        }

        store.name = storeDto.name
        store.contact = contact

        storeRepository.save(store)
        return store.toDto()
    }

    fun deleteStoreById(id: Long) {
        if (!storeRepository.existsById(id)) {
            throw IllegalArgumentException("Store not found")
        }
        storeRepository.deleteById(id)
    }

    fun addProductToStore(storeId: Long, productDto: ProductDto): ProductDto {
        // Retrieve the store entity from the database
        var store: StoreEntity = storeRepository.findById(storeId).orElse(null)
            ?: throw IllegalArgumentException("Store not found")

        // Check if the product is already in the store, if so, update the quantity
        var product: ProductEntity? = store.products.find { it.id == productDto.id }
        if (product != null) {
            product.quantity += productDto.quantity
        } else {
            // Check if the product exists
            val result = webClient.get().uri("api/v1/products/${productDto.id}").retrieve().bodyToMono(ProductDto::class.java).block()
                ?: throw IllegalArgumentException("Product not found")

            product = ProductEntity(productDto.id, result.name, productDto.quantity)
            store.products += product
        }

        storeRepository.save(store)
        return product.toDto()
    }

    fun removeProductFromStore(storeId: Long, productId: String, quantity: Int): ProductDto {
        // Retrieve the store entity from the database
        var store: StoreEntity = storeRepository.findById(storeId).orElse(null)
            ?: throw IllegalArgumentException("Store not found")

        // Check if the product is in the store
        var product: ProductEntity? = store.products.find { it.id == productId }
        if (product != null) {
            if (product.quantity <= quantity) {
                store.products -= product
            } else {
                product.quantity -= quantity
            }
        } else {
            throw IllegalArgumentException("Product not found in store")
        }

        storeRepository.save(store)
        return product.toDto()
    }

    fun deleteProductFromStore(storeId: Long, productId: String) {
        // Retrieve the store entity from the database
        var store: StoreEntity = storeRepository.findById(storeId).orElse(null)
            ?: throw IllegalArgumentException("Store not found")

        // Check if the product is in the store
        var product: ProductEntity? = store.products.find { it.id == productId }
        if (product != null) {
            store.products -= product
        } else {
            throw IllegalArgumentException("Product not found in store")
        }

        storeRepository.save(store)
    }

    fun findProductOverview(productId: String): ProductOverviewDto {
        val stores = storeRepository.findAll().filter { it.products.any { it.id == productId } }

        /* Per store quantity */
        val perStore = stores.map {
            val product = it.products.find { it.id == productId }!!
            StoreProductOverviewDto(it.id, product.quantity)
        }

        /* Total quantity */
        val totalQuantity = perStore.stream().mapToInt { it.quantity }.sum()

        return ProductOverviewDto(perStore, totalQuantity)
    }

}
