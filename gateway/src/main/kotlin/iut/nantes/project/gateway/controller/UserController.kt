package iut.nantes.project.gateway.controller

import iut.nantes.project.gateway.dto.UserDto
import iut.nantes.project.gateway.model.UserEntity
import iut.nantes.project.gateway.service.CustomUserDetailsService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private fun UserDto.toEntity() = UserEntity(login, BCryptPasswordEncoder().encode(password), isAdmin)
private fun UserEntity.toDto() = UserDto(login, password, isAdmin)

@RestController
class UserController(
    private val userDetailsService: CustomUserDetailsService

) {
    @PostMapping("/api/v1/user")
    fun createUser(@RequestBody user: UserDto) : ResponseEntity<UserDto> {
        val userEntity = userDetailsService.createUser(user.toEntity())
        return ResponseEntity.ok(userEntity.toDto())
    }
}