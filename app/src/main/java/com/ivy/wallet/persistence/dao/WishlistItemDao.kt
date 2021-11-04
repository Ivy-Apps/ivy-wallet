package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.WishlistItem
import java.util.*

@Dao
interface WishlistItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: WishlistItem)

    @Query("SELECT * FROM wishlist_items ORDER BY orderNum ASC")
    fun findAll(): List<WishlistItem>

    @Query("SELECT * FROM wishlist_items WHERE id = :id")
    fun findById(id: UUID): WishlistItem?

    @Query("DELETE FROM wishlist_items WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM wishlist_items")
    fun deleteAll()
}