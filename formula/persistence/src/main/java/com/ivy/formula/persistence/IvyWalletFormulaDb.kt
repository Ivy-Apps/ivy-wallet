package com.ivy.formula.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.core.persistence.GeneralTypeConverters
import com.ivy.formula.persistence.dao.DataSourceDao
import com.ivy.formula.persistence.dao.FormulaDao
import com.ivy.formula.persistence.dao.ProjectDao
import com.ivy.formula.persistence.dao.ProjectFormulaLinkDao
import com.ivy.formula.persistence.entity.datasource.DataSourceEntity
import com.ivy.formula.persistence.entity.formula.FormulaEntity
import com.ivy.formula.persistence.entity.project.ProjectEntity
import com.ivy.formula.persistence.entity.project.ProjectFormulaLinkEntity

@Database(
    entities = [
        FormulaEntity::class, ProjectEntity::class,
        ProjectFormulaLinkEntity::class, DataSourceEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(GeneralTypeConverters::class)
abstract class IvyWalletFormulaDb : RoomDatabase() {
    abstract fun formulaDao(): FormulaDao

    abstract fun projectDao(): ProjectDao

    abstract fun projectFormulaLinkDao(): ProjectFormulaLinkDao

    abstract fun dataSourceDao(): DataSourceDao


    companion object {
        private const val DB_NAME = "ivy-wallet-formula.db"

        fun create(applicationContext: Context): IvyWalletFormulaDb {
            return Room.databaseBuilder(
                applicationContext, IvyWalletFormulaDb::class.java, DB_NAME
            ).build()
        }
    }
}