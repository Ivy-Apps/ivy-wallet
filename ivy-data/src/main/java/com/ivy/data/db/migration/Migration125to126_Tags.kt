package com.ivy.domain.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration125to126_Tags : Migration(125, 126) {
    companion object {
        private const val TAGS_TABLE = "tags"
        private const val TAGS_TRANSACTION_TABLE = "tags_transaction"
    }
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `${TAGS_TABLE}` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `color` INTEGER NOT NULL, `icon` TEXT, `orderNum` REAL NOT NULL, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `dateTime` INTEGER, `featureBit` INTEGER NOT NULL, `featureBitValue` TEXT, PRIMARY KEY(`id`))"
        )

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `${TAGS_TRANSACTION_TABLE}` (`tagId` TEXT NOT NULL, `associatedId` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, PRIMARY KEY(`tagId`, `associatedId`))"
            )
    }
}
