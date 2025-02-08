package iut.nantes.project.stores

import iut.nantes.project.stores.dto.AddressDto
import iut.nantes.project.stores.dto.ContactDto
import iut.nantes.project.stores.service.ContactService
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var service: ContactService

    @BeforeEach
    fun beforeEachTest() {
        service.findAllContacts().forEach { service.deleteContactById(it.id!!) }

    }

    @Test
    fun `POST contact - should return 201 Created`() {
        mvc.post("/api/v1/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("my@email.com", "0123456789", "Rue truc", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.email") { value("my@email.com") }
        }
    }

    @Test
    fun `POST contact with invalid email - should return 400`() {
        mvc.post("/api/v1/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("invalid-email", "0123456789", "Rue truc", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `POST contact with invalid phone - should return 400`() {
        mvc.post("/api/v1/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("my@email.com", "123", "Rue truc", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `POST contact with invalid address - should return 400`() {
        mvc.post("/api/v1/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("my@email.com", "0123456789", "Rue", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET all contacts - should return 200`() {
        service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.get("/api/v1/contacts") {
            header("X-USER", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET contacts by city - should return 200`() {
        service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.get("/api/v1/contacts?city=Nantes") {
            header("X-USER", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET contact by ID - should return 200 if exists`() {
        val contact = service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.get("/api/v1/contacts/${contact.id}") {
            header("X-USER", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.email") { value(contact.email) }
        }
    }

    @Test
    fun `GET contact by ID - should return 404 if not exists`() {
        mvc.get("/api/v1/contacts/999") {
            header("X-USER", "test")
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `PUT update contact - should return 200 if update is successful`() {
        val contact = service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.put("/api/v1/contacts/${contact.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("updated@email.com", "0123456789", "Rue truc", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.email") { value("updated@email.com") }
        }
    }

    @Test
    fun `PUT update contact - should return 400 if email and phone are changed`() {
        val contact = service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.put("/api/v1/contacts/${contact.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = contact("updated@email.com", "9876543210", "Rue truc", "Nantes", "44300")
            header("X-USER", "test")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `DELETE contact - should return 204 if deleted successfully`() {
        val contact = service.saveContact(ContactDto(null, "my@email.com", "0123456789", AddressDto("Rue truc", "Nantes", "44300")))

        mvc.delete("/api/v1/contacts/${contact.id}") {
            header("X-USER", "test")
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `DELETE contact - should return 404 if contact does not exist`() {
        mvc.delete("/api/v1/contacts/999") {
            header("X-USER", "test")
        }.andExpect {
            status { isNotFound() }
        }
    }

    private fun contact(email: String, phone: String, street: String, city: String, postalCode: String) = """
        {
            "email": "$email",
            "phone": "$phone",
            "address": {
                "street": "$street",
                "city": "$city",
                "postalCode": "$postalCode"
            }
        }
    """.trimIndent()
}
