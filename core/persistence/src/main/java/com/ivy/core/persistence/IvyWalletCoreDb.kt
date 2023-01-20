package com.ivy.core.persistence

import android.content.Context
import androidx.room.*
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import com.ivy.core.persistence.algorithm.calc.CalcTrnDao
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnDao
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.core.persistence.dao.tag.TagDao
import com.ivy.core.persistence.dao.trn.TransactionDao
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
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.core.persistence.entity.trn.converter.TrnTypeConverters
import com.ivy.core.persistence.migration.Migration1to2_LastUpdated

@Database(
    entities = [
        TransactionEntity::class, TrnLinkRecordEntity::class,
        TrnMetadataEntity::class, AttachmentEntity::class,
        AccountEntity::class, AccountFolderEntity::class,
        CategoryEntity::class, ExchangeRateEntity::class,
        ExchangeRateOverrideEntity::class, TagEntity::class,
        TrnTagEntity::class, AccountCacheEntity::class,
    ],
    views = [
        CalcHistoryTrnView::class
    ],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
    ],
    exportSchema = true,
)
@TypeConverters(
    GeneralTypeConverters::class,
    TrnTypeConverters::class, AttachmentTypeConverters::class,
    AccountTypeConverter::class, CategoryTypeConverter::class,
    ExchangeRateTypeConverter::class,
)
abstract class IvyWalletCoreDb : RoomDatabase() {
    abstract fun trnDao(): TransactionDao
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

    abstract fun calcTrnDao(): CalcTrnDao
    abstract fun calcHistoryTrnDao(): CalcHistoryTrnDao
    abstract fun ratesDao(): RatesDao
    abstract fun accountCacheDao(): AccountCacheDao

    companion object {
        private const val DB_NAME = "ivy-wallet-core.db"

        fun create(applicationContext: Context): IvyWalletCoreDb {
            return Room.databaseBuilder(
                applicationContext, IvyWalletCoreDb::class.java, DB_NAME
            ).addMigrations(
                Migration1to2_LastUpdated()
            ).build()
        }
    }
}