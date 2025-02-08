package iut.nantes.project.products

import iut.nantes.project.products.dto.PriceDTO
import iut.nantes.project.products.dto.ProductDTO
import iut.nantes.project.products.service.FamilyService
import iut.nantes.project.products.service.ProductService
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class FamilyControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var service: FamilyService

    @Autowired
    lateinit var productService: ProductService

    @BeforeEach
    fun beforeEachTest() {
        productService.findAll()
            .forEach { productService.delete(it.id!!) }
        service.findAll().forEach {
            service.deleteById(it.id!!)
        }
    }

    @Test
    fun `POST family - should return 201 Created`() {
        mvc.post("/api/v1/families") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Bike", "La famille des vélos")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Bike") }
        }
    }

    @Test
    fun `POST family without specifying name - should return 404`() {
        mvc.post("/api/v1/families") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "description": "Une description" }""".trimIndent()
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }
    @Test
    fun `POST family with too short name - should return 404`() {
        mvc.post("/api/v1/families") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Bi", "La famille des vélos")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `POST family with already used name - should return 409`() {
        service.addFamily("Bike", "La famille des vélos")

        mvc.post("/api/v1/families") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Bike", "La famille des vélos")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(409) }
        }
    }

    @Test
    fun `GET all families - should return 200`() {
        service.addFamily("Bike", "La famille des vélos")

        mvc.get("/api/v1/families") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET family by ID - should return 200 if exists`() {
        val family = service.addFamily("Bike", "La famille des vélos")

        mvc.get("/api/v1/families/${family!!.id!!}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value(family.name) }
        }
    }

    @Test
    fun `GET family by ID - should return 400 if ID format is invalid`() {
        mvc.get("/api/v1/families/sdfsdfsfd") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET family by ID - should return 404 if family does not exist`() {
        mvc.get("/api/v1/families/${UUID.randomUUID()}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `PUT update family - should return 200 if update is successful`() {
        val family = service.addFamily("Bike", "La famille des vélos")

        mvc.put("/api/v1/families/${family!!.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Updated Bike","Updated description")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Updated Bike") }
        }
    }

    @Test
    fun `PUT update family - should return 400 if data is invalid`() {
        mvc.put("/api/v1/families/${UUID.randomUUID()}") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Updated Bike","Updated description")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `PUT update family - should return 409 if name conflict`() {
        service.addFamily("Bike", "La famille des vélos")
        val family = service.addFamily("Bike2", "La deuxième famille des vélos")
        mvc.put("/api/v1/families/${family!!.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Bike","Updated description")
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(409) }
        }
    }

    @Test
    fun `DELETE family - should return 204 if deleted successfully`() {
        val family = service.addFamily("Bike", "La famille des vélos")
        mvc.delete("/api/v1/families/${family!!.id!!}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(204) }
        }
    }

    @Test
    fun `DELETE family - should return 409 if products are linked`() {
        val family = service.addFamily("Bike", "La famille des vélos")
        productService.create(ProductDTO(null, "Velo de compet", "velo masterclass", PriceDTO(100, "EUR"), family!!))

        mvc.delete("/api/v1/families/${family.id!!}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(409) }
        }
    }

    @Test
    fun `DELETE family - should return 404 if family does not exist`() {
        mvc.delete("/api/v1/families/${UUID.randomUUID()}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isNotFound() }
        }
    }

    private fun family(name: String, description: String) = """
        {
            "name": "$name",
            "description": "$description"
        }
    """.trimIndent()
}