package iut.nantes.project.products.config

import iut.nantes.project.products.utils.RequestFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductsFilterConfig {
    @Bean
    fun productsRequestFilter(): RequestFilter {
        return RequestFilter()
    }
}