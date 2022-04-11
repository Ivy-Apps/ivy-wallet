package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration108to109_Users : Migration(108, 109) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`email` TEXT NOT NULL, `authProviderType` TEXT NOT NULL, `firstName` TEXT NOT NULL, `lastName` TEXT, `profilePicture` TEXT, `color` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}