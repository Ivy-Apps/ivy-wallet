package com.ivy.core.persistence.di

import android.content.Context
import com.ivy.core.persistence.IvyWalletCoreDb
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CorePersistenceModuleDI {
    @Provides
    @Singleton
    fun provideIvyWalletDb(@ApplicationContext appContext: Context): IvyWalletCoreDb =
        IvyWalletCoreDb.create(appContext)

    @Provides
    @Singleton
    fun provideAccountDao(db: IvyWalletCoreDb): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideAccountFolderDao(db: IvyWalletCoreDb): AccountFolderDao = db.accountFolderDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: IvyWalletCoreDb): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideExchangeRateDao(db: IvyWalletCoreDb): ExchangeRateDao = db.exchangeRateDao()

    @Provides
    @Singleton
    fun provideExchangeRateOverrideDao(db: IvyWalletCoreDb): ExchangeRateOverrideDao =
        db.exchangeRateOverrideDao()

    @Provides
    @Singleton
    fun provideTagDao(db: IvyWalletCoreDb): TagDao = db.tagDao()

    @Provides
    @Singleton
    fun provideTrnDao(db: IvyWalletCoreDb): TrnDao = db.trnDao()

    @Provides
    @Singleton
    fun provideTrnLinkRecordDao(db: IvyWalletCoreDb): TrnLinkRecordDao = db.trnLinkRecordDao()

    @Provides
    @Singleton
    fun provideTrnMetadataDao(db: IvyWalletCoreDb): TrnMetadataDao = db.trnMetadataDao()

    @Provides
    @Singleton
    fun provideTrnTagDao(db: IvyWalletCoreDb): TrnTagDao = db.trnTagDao()

    @Provides
    @Singleton
    fun provideAttachmentDao(db: IvyWalletCoreDb): AttachmentDao = db.attachmentDao()
}