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
    fun save(value: SettingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<SettingsEntity>)

    @Query("SELECT * FROM SettingsEntity LIMIT 1")
    fun findFirst(): SettingsEntity

    @Query("SELECT * FROM SettingsEntity")
    fun findAll(): List<SettingsEntity>

    @Query("SELECT * FROM SettingsEntity WHERE id = :id")
    fun findById(id: UUID): SettingsEntity?

    @Query("DELETE FROM SettingsEntity WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM SettingsEntity")
    fun deleteAll()
}