package com.ivy.wallet.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    val name: String,
    val price: Double,
    val accountId: UUID,
    val categoryId: UUID? = null,
    val description: String?,
    val plannedDateTime: LocalDateTime? = null,
    val orderNum: Double = 0.0,
    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)