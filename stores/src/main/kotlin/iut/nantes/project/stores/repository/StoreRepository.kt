package iut.nantes.project.stores.repository

import iut.nantes.project.stores.model.StoreEntity
import org.springframework.data.jpa.repository.JpaRepository


interface StoreRepository : JpaRepository<StoreEntity, Long>