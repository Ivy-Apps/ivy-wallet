package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.Settings
import java.util.*

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: Settings)

    @Query("SELECT * FROM settings LIMIT 1")
    fun findFirst(): Settings

    @Query("SELECT * FROM settings")
    fun findAll(): List<Settings>

    @Query("SELECT * FROM settings WHERE id = :id")
    fun findById(id: UUID): Settings?

    @Query("DELETE FROM settings WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM settings")
    fun deleteAll()
}