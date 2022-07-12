package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration120to121_DropWishlistItem : Migration(120, 121) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE wishlist_items")
    }
}