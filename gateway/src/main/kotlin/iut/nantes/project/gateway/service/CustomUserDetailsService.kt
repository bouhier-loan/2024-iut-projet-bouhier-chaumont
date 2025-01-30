package iut.nantes.project.gateway.service

import iut.nantes.project.gateway.model.UserEntity
import iut.nantes.project.gateway.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    @Value("\${gateway.security:db}") private val securityMode: String
) : UserDetailsService {

    @PostConstruct
    fun init() {
        if (securityMode == "inmemory") {
            inMemoryUsers["ADMIN"] = UserEntity("ADMIN", BCryptPasswordEncoder().encode("ADMIN"), true)
        } else {
            if (!userRepository.existsById("ADMIN")) {
                userRepository.save(UserEntity("ADMIN", BCryptPasswordEncoder().encode("ADMIN"), true))
            }
        }
    }

    private val inMemoryUsers = mutableMapOf<String, UserEntity>()

    override fun loadUserByUsername(username: String): UserDetails {
        val user = when(securityMode) {
            "inmemory" -> inMemoryUsers[username]
            else -> userRepository.findById(username).orElse(null)
        } ?: throw UsernameNotFoundException("User not found")

        if (securityMode == "inmemory") println(inMemoryUsers)

        return org.springframework.security.core.userdetails.User
            .withUsername(user.login)
            .password(user.password)
            .roles(if (user.isAdmin) "ADMIN" else "USER")
            .build()
    }

    fun createUser(user: UserEntity) : UserEntity{
        when(securityMode) {
            "inmemory" -> inMemoryUsers[user.login] = user
            else -> userRepository.save(user)
        }
        return user
    }
}