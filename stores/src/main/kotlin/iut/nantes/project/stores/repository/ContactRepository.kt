package iut.nantes.project.stores.repository

import iut.nantes.project.stores.model.ContactEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository : JpaRepository<ContactEntity, Long>