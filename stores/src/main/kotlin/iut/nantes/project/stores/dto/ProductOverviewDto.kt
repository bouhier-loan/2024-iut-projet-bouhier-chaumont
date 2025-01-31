package iut.nantes.project.stores.dto

data class StoreProductOverviewDto(
    val storeId: Long?,
    val quantity: Int
)

data class ProductOverviewDto(
    val stores: List<StoreProductOverviewDto>,
    val totalQuantity: Int
)