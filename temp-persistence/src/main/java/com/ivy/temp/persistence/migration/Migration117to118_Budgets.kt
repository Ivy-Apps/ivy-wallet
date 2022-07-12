package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration117to118_Budgets : Migration(117, 118) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tableName = "budgets"
        database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}` (`name` TEXT NOT NULL, `amount` REAL NOT NULL, `categoryIdsSerialized` TEXT, `accountIdsSerialized` TEXT, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `orderId` REAL NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }

}