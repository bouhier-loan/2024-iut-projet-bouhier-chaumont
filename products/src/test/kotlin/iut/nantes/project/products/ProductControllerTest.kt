package iut.nantes.project.products

import iut.nantes.project.products.dto.FamilyDTO
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
import java.util.*
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var familyService: FamilyService

    @Autowired
    lateinit var productService: ProductService

    @BeforeEach
    fun beforeEachTest() {
        productService.findAll()
            .forEach { productService.delete(it.id!!) }
        familyService.findAll()
            .forEach { familyService.deleteById(it.id!!) }
    }

    @Test
    fun `POST product - should return 201 if created successfully`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")

        mvc.post("/api/v1/products") {
            contentType = MediaType.APPLICATION_JSON
            content = product("Vélo de compet", "Un velo masterclass", 100, "EUR", family!!)
            header("X-User", "ADMIN")
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Vélo de compet") }
        }
    }

    @Test
    fun `POST product - should return 400 if family does not exist`() {
        mvc.post("/api/v1/products") {
            contentType = MediaType.APPLICATION_JSON
            content = product("Vélo de compet", "Un velo masterclass", 100,
                "EUR", FamilyDTO(UUID.randomUUID(), "Bike", "Famille de vélo"))
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `POST product - should return 400 if data is invalid`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")

        mvc.post("/api/v1/products") {
            contentType = MediaType.APPLICATION_JSON
            content = product("V", "Un velo masterclass", 100, "EUR", family!!)
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET products - should return 200`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        productService.create(ProductDTO(null, "Velo de compet", "velo masterclass",
            PriceDTO(100, "EUR"), family!!))

        mvc.get("/api/v1/products") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }


    @Test
    fun `GET products - should return 200 with filtered list`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        productService.create(ProductDTO(null, "Velo de compet", "velo masterclass",
            PriceDTO(100, "EUR"), family!!))
        productService.create(ProductDTO(null, "Velo de compet 2", "velo masterclass",
            PriceDTO(300, "EUR"), family))

        mvc.get("/api/v1/products?minprice=100&maxprice=200") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `GET products - should return 400 if filter criteria are invalid`() {
        mvc.get("/api/v1/products?minprice=200&maxprice=100") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET product by ID - should return 200 if product exists`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet", "velo masterclass",
            PriceDTO(100, "EUR"), family!!))

        mvc.get("/api/v1/products/${product!!.id}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value(product.name) }
        }

    }

    @Test
    fun `GET product by ID - should return 400 if ID format is invalid`() {
        mvc.get("/api/v1/products/sdfsdfsdf") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET product by ID - should return 404 if product does not exist`() {
        mvc.get("/api/v1/products/${UUID.randomUUID()}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isNotFound()  }
        }
    }

    @Test
    fun `PUT update product - should return 200 if updated successfully`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet", "velo masterclass",
            PriceDTO(100, "EUR"), family!!))

        mvc.put("/api/v1/products/${product!!.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = product("Velo de vacances", "velo masterclass", 500, "DOL", family)
            header("X-User", "ADMIN")
        }.andExpect {
            status { isEqualTo(200) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Velo de vacances") }
        }
    }

    @Test
    fun `PUT update product - should return 400 if data is invalid`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet",
            "velo masterclass", PriceDTO(100, "EUR"), family!!))

        mvc.put("/api/v1/products/${product!!.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = product("Velo de vacances", "velo masterclass", 500, "DOLLAR", family)
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `PUT update product - should return 400 if family does not exist`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet",
            "velo masterclass", PriceDTO(100, "EUR"), family!!))

        mvc.put("/api/v1/products/${product!!.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = product("Velo de vacances", "velo masterclass", 100, "EUR",
                FamilyDTO(UUID.randomUUID(), "Bike25", "Famille de vélos")
            )
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `DELETE product - should return 204 if deleted successfully`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet",
            "velo masterclass", PriceDTO(100, "EUR"), family!!))

        mvc.put("/api/v1/products/${product!!.id}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `DELETE product - should return 400 if ID is invalid`() {
        mvc.put("/api/v1/products/${UUID.randomUUID()}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `DELETE product - should return 409 if product is still in stock`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")
        val product = productService.create(ProductDTO(null, "Velo de compet",
            "velo masterclass", PriceDTO(100, "EUR"), family!!))

        mvc.put("/api/v1/products/${product!!.id}") {
            header("X-User", "ADMIN")
        }.andExpect {
            status { isNoContent() }
        }
    }




    private fun family(name: String, description: String) = """
        {
            "name": "$name",
            "description": "$description"
        }
    """.trimIndent()


    private fun product(name: String, description: String, amount: Int, currency: String, familyDTO: FamilyDTO) = """
        {
            "name": "$name",
            "description": "$description",
            "price": {
                "amount": $amount,
                "currency": "$currency"
            },
            "family": {
                "id": "${familyDTO.id}",
                "name": "${familyDTO.name}",
                "description": "${familyDTO.description}"
            }
        }
    """.trimIndent()

    private fun store() = """
        {
          "id": 1,
          "name": "Atlantis",
          "contact": {
            "id": 1,
            "email": "my@email.com",
            "phone": "0123456789",
            "address": {
              "street": "Rue truc",
              "city": "Nantes",
              "postalCode": "44300"
            }
          },
          "products": [
            {
              "id": "e437f62a-432e-4aef-a440-6c86d3b09901",
              "name": "RC 500",
              "quantity": 1
            }
          ]
        }
    """.trimIndent()
}