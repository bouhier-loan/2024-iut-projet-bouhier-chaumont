package iut.nantes.project.products.dto

import java.util.*

data class StoreQuantityDTO (
    val storeId: Int,
    val quantity: Int
)

data class StoreResponseDTO (
    val stores: List<StoreQuantityDTO>,
    val totalQuantity: Int
)