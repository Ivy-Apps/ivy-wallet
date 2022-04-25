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

    @Query("SELECT * FROM settings LIMIT 1")
    fun findFirst(): SettingsEntity

    @Query("SELECT * FROM settings")
    fun findAll(): List<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = :id")
    fun findById(id: UUID): SettingsEntity?

    @Query("DELETE FROM settings WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM settings")
    fun deleteAll()
}