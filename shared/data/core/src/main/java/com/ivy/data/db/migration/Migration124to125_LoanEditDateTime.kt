package com.ivy.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration124to125_LoanEditDateTime : Migration(124, 125) {
    companion object {
        private const val LOANS_TABLE = "loans"
        private const val LOANS_TEMP_TABLE = "loans_temp"
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        val columns = "name, amount, type, color, icon, orderNum, accountId, isSynced, isDeleted, dateTime, id"
        db.execSQL(
            "CREATE TABLE `${LOANS_TEMP_TABLE}` (`name` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `color` INTEGER NOT NULL, `icon` TEXT, `orderNum` REAL NOT NULL, `accountId` TEXT, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `dateTime` INTEGER, `id` TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
        db.execSQL("INSERT INTO `${LOANS_TEMP_TABLE}` ($columns ) SELECT $columns FROM `${LOANS_TABLE}`")
        db.execSQL("DROP TABLE `${LOANS_TABLE}`")
        db.execSQL("ALTER TABLE `${LOANS_TEMP_TABLE}` RENAME TO `${LOANS_TABLE}`")
    }
}