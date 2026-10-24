package com.example.data

import kotlinx.coroutines.flow.Flow

class CeramicRepository(private val dao: CeramicDao) {
    val cartItems: Flow<List<CartItemEntity>> = dao.getAllCartItems()
    val wishlistItems: Flow<List<WishlistEntity>> = dao.getAllWishlistItems()
    val orders: Flow<List<OrderEntity>> = dao.getAllOrders()

    suspend fun addOrIncrementCart(productId: Int, glaze: GlazeStyle, addCount: Int = 1) {
        val existing = dao.findCartItem(productId, glaze.name)
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + addCount))
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    productId = productId,
                    selectedGlaze = glaze.name,
                    quantity = addCount
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItem: CartItemEntity, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItemById(cartItem.id)
        } else {
            dao.updateCartItem(cartItem.copy(quantity = newQuantity))
        }
    }

    suspend fun removeCartItem(cartItemId: Int) {
        dao.deleteCartItemById(cartItemId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun toggleWishlist(productId: Int): Boolean {
        val exists = dao.isWishlisted(productId)
        return if (exists) {
            dao.deleteWishlist(productId)
            false
        } else {
            dao.insertWishlist(WishlistEntity(productId = productId))
            true
        }
    }

    suspend fun placeOrder(order: OrderEntity) {
        dao.insertOrder(order)
        dao.clearCart()
    }
}
