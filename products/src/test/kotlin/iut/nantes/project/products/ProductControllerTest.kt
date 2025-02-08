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
        familyService.findAll().forEach {
            familyService.deleteById(it.id!!)
        }
    }

    @Test
    fun `POST product - should return 201 if created successfully`() {
        val family = familyService.addFamily("Bike", "La famille des vélos")

        println(family)

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
}