package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.SettingsEntity
import java.util.*

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: SettingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<SettingsEntity>)

    @Query("UPDATE settings SET currency = :currencyCode")
    suspend fun updateBaseCurrency(currencyCode: String)

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun findFirst(): SettingsEntity

    @Query("SELECT * FROM settings")
    suspend fun findAll(): List<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = :id")
    suspend fun findById(id: UUID): SettingsEntity?

    @Query("DELETE FROM settings WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM settings")
    suspend fun deleteAll()
}