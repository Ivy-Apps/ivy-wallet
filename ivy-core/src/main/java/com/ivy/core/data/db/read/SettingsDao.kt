package com.ivy.core.data.db.read

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.data.db.entity.SettingsEntity
import java.util.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun findFirst(): SettingsEntity

    @Query("SELECT * FROM settings")
    suspend fun findAll(): List<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = :id")
    suspend fun findById(id: UUID): SettingsEntity?
}
