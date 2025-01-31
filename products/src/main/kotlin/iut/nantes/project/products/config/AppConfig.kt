package iut.nantes.project.products.config

import iut.nantes.project.products.repository.*
import iut.nantes.project.products.service.FamilyService
import iut.nantes.project.products.service.ProductService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class AppConfig {

    @Bean
    @Profile("!dev")
    fun h2ProductRepository(jpaRepository: ProductJpaRepository): IProductRepository = H2ProductRepository(jpaRepository)

    @Bean
    @Profile("dev")
    fun hashMapProductRepository() : IProductRepository = HashMapProductRepository()

    @Bean
    fun productService(productRepository: IProductRepository, familyService: FamilyService) = ProductService(productRepository, familyService)

    @Bean
    @Profile("!dev")
    fun h2FamilyRepository(jpaRepository: FamilyJpaRepository): IFamilyRepository = H2FamilyRepository(jpaRepository)

    @Bean
    @Profile("dev")
    fun hashMapFamilyRepository() : IFamilyRepository = HashMapFamilyRepository()

    @Bean
    fun familyService(familyRepository: IFamilyRepository) = FamilyService(familyRepository)

}