package iut.nantes.project.products

import iut.nantes.project.products.service.FamilyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class FamilyControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var service: FamilyService

    @Test
    fun `Test create family successfully`() {
        mvc.post("/api/v1/family") {
            contentType = MediaType.APPLICATION_JSON
            content = family("Bike", "La famille des vélos")
            header("X-USER", "ADMIN")
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Bike") }
        }
    }

    private fun family(name: String, description: String) = """
        {
            "name": "$name",
            "description": "$description"
        }
    """.trimIndent()


}