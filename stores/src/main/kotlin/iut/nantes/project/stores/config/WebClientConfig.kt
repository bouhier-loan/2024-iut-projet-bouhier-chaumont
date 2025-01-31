package iut.nantes.project.stores.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .defaultHeader("X-USER", "system")
            .baseUrl("http://localhost:8081")
            .build()
    }
}