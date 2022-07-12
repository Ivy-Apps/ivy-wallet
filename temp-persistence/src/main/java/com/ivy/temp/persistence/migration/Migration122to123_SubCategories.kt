package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration122to123_SubCategories : Migration(122, 123) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE categories ADD COLUMN parentCategoryId TEXT")
    }
}