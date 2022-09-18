package com.ivy.core.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.core.persistence.dao.tag.TagDao
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.dao.trn.TrnMetadataDao
import com.ivy.core.persistence.dao.trn.TrnTagDao
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.core.persistence.entity.account.converter.AccountTypeConverter
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.attachment.converter.AttachmentTypeConverters
import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.core.persistence.entity.category.converter.CategoryTypeConverter
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.core.persistence.entity.exchange.converter.ExchangeRateTypeConverter
import com.ivy.core.persistence.entity.tag.TagEntity
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.core.persistence.entity.trn.converter.TrnTypeConverters

@Database(
    entities = [
        TrnEntity::class, TrnLinkRecordEntity::class,
        TrnMetadataEntity::class, AttachmentEntity::class,
        AccountEntity::class, AccountFolderEntity::class,
        CategoryEntity::class, ExchangeRateEntity::class,
        ExchangeRateOverrideEntity::class, TagEntity::class,
        TrnTagEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    GeneralTypeConverters::class,
    TrnTypeConverters::class, AttachmentTypeConverters::class,
    AccountTypeConverter::class, CategoryTypeConverter::class,
    ExchangeRateTypeConverter::class,
)
abstract class IvyWalletCoreDb : RoomDatabase() {
    abstract fun trnDao(): TrnDao

    abstract fun trnLinkRecordDao(): TrnLinkRecordDao

    abstract fun trnMetadataDao(): TrnMetadataDao

    abstract fun trnTagDao(): TrnTagDao

    abstract fun attachmentDao(): AttachmentDao

    abstract fun accountDao(): AccountDao

    abstract fun accountFolderDao(): AccountFolderDao

    abstract fun categoryDao(): CategoryDao

    abstract fun tagDao(): TagDao

    abstract fun exchangeRateDao(): ExchangeRateDao

    abstract fun exchangeRateOverrideDao(): ExchangeRateOverrideDao

    companion object {
        private const val DB_NAME = "ivy-wallet-core.db"

        fun create(applicationContext: Context): IvyWalletCoreDb {
            return Room.databaseBuilder(
                applicationContext, IvyWalletCoreDb::class.java, DB_NAME
            ).build()
        }
    }
}