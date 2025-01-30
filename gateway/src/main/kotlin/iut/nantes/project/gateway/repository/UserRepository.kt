package iut.nantes.project.gateway.repository

import iut.nantes.project.gateway.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, String>