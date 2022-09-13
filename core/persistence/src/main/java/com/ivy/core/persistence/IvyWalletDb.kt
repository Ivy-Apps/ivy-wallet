package com.ivy.core.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.core.persistence.dao.TrnDao
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.converter.TrnTypeConverters

@Database(
    entities = [TrnEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(GeneralTypeConverters::class, TrnTypeConverters::class)
abstract class IvyWalletDb : RoomDatabase() {
    abstract fun trnDao(): TrnDao
}