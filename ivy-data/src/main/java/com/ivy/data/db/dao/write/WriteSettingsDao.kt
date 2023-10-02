package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.SettingsEntity
import java.util.UUID

@Dao
interface WriteSettingsDao {
    @Upsert
    suspend fun save(value: SettingsEntity)

    @Upsert
    suspend fun saveMany(value: List<SettingsEntity>)

    @Query("DELETE FROM settings WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM settings")
    suspend fun deleteAll()
}