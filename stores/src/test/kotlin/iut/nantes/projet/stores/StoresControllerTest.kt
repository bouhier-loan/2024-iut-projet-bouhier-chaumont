package iut.nantes.project.stores

import iut.nantes.project.stores.dto.AddressDto
import iut.nantes.project.stores.dto.ContactDto
import iut.nantes.project.stores.dto.StoreDto
import iut.nantes.project.stores.service.ContactService
import iut.nantes.project.stores.service.StoreService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StoreControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var storeService: StoreService

    @Autowired
    lateinit var contactService: ContactService

    @BeforeEach
    fun beforeEachTest() {
        storeService.findAllStores().forEach { storeService.deleteStoreById(it.id!!) }
        contactService.findAllContacts().forEach { contactService.deleteContactById(it.id!!) }
    }

    @Test
    fun `POST store - should return 201 Created`() {
        val contact = contactService.saveContact(createContact())

        mvc.post("/api/v1/stores") {
            contentType = MediaType.APPLICATION_JSON
            content = store("Atlantis", contact)
            header("X-User", "test")
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Atlantis") }
        }
    }

    @Test
    fun `POST store with invalid data - should return 400`() {
        val contact = contactService.saveContact(createContact())
        mvc.post("/api/v1/stores") {
            contentType = MediaType.APPLICATION_JSON
            content = store("At", contact)
            header("X-User", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET all stores - should return 200`() {
        val contact = contactService.saveContact(createContact())
        storeService.saveStore(StoreDto(null, "Atlantis", contact))

        mvc.get("/api/v1/stores") {
            header("X-User", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET store by ID - should return 200 if exists`() {
        val contact = contactService.saveContact(createContact())
        val store = storeService.saveStore(StoreDto(null, "Atlantis", contact))

        mvc.get("/api/v1/stores/${store.id}") {
            header("X-User", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value(store.name) }
        }
    }

    @Test
    fun `GET store by ID - should return 400 if ID format is invalid`() {
        mvc.get("/api/v1/stores/invalid-id") {
            header("X-User", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET store by ID - should return 404 if store does not exist`() {
        mvc.get("/api/v1/stores/13980887483") { // Random ID
            header("X-User", "test")
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `PUT update store - should return 200 if update is successful`() {
        val contact = contactService.saveContact(createContact())
        val store = storeService.saveStore(StoreDto(null, "Atlantis", contact))

        mvc.put("/api/v1/stores/${store.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = store("Updated Atlantis", contact)
            header("X-User", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Updated Atlantis") }
        }
    }

    @Test
    fun `PUT update store - should return 400 if data is invalid`() {
        val contact = contactService.saveContact(createContact())
        val store = storeService.saveStore(StoreDto(null, "Atlantis", contact))

        mvc.put("/api/v1/stores/${store.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = store("Up", contact)
            header("X-User", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `DELETE store - should return 204 if deleted successfully`() {
        val contact = contactService.saveContact(createContact())
        val store = storeService.saveStore(StoreDto(null, "Atlantis", contact))

        mvc.delete("/api/v1/stores/${store.id}") {
            header("X-User", "test")
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `DELETE store - should return 404 if store does not exist`() {
        mvc.delete("/api/v1/stores/13980887483") { // Random ID
            header("X-User", "test")
        }.andExpect {
            status { isNotFound() }
        }
    }

    private fun createContact() = ContactDto(
            null,
            "my@email.com",
            "0123456789",
            AddressDto("Rue truc", "Nantes", "44300")
        )


    private fun store(name: String, contact: ContactDto) = """
        {
            "name": "$name",
            "contact": {
                "email": "${contact.email}",
                "phone": "${contact.phone}",
                "address": {
                    "street": "${contact.address.street}",
                    "city": "${contact.address.city}",
                    "postalCode": "${contact.address.postalCode}"
                }
            }
        }
    """.trimIndent()
}
