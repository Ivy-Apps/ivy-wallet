package com.ivy.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Suppress("MagicNumber", "ClassNaming")
class Migration127to128_PaidForDateRecord : Migration(127, 128) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE transactions ADD COLUMN paidForDateTime INTEGER")
    }
}