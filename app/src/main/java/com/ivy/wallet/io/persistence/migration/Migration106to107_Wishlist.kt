package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration106to107_Wishlist : Migration(106, 107) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE wishlist_items")
        database.execSQL("CREATE TABLE IF NOT EXISTS `wishlist_items` (`name` TEXT NOT NULL, `price` REAL NOT NULL, `accountId` TEXT NOT NULL, `categoryId` TEXT, `description` TEXT, `plannedDateTime` INTEGER, `orderNum` REAL NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}