package com.ivy.core.persistence.di

import android.content.Context
import com.ivy.core.persistence.IvyWalletDb
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
    fun provideIvyWalletDb(@ApplicationContext appContext: Context): IvyWalletDb =
        IvyWalletDb.create(appContext)

    @Provides
    @Singleton
    fun provideAccountDao(db: IvyWalletDb): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideAccountFolderDao(db: IvyWalletDb): AccountFolderDao = db.accountFolderDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: IvyWalletDb): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideExchangeRateDao(db: IvyWalletDb): ExchangeRateDao = db.exchangeRateDao()

    @Provides
    @Singleton
    fun provideExchangeRateOverrideDao(db: IvyWalletDb): ExchangeRateOverrideDao =
        db.exchangeRateOverrideDao()

    @Provides
    @Singleton
    fun provideTagDao(db: IvyWalletDb): TagDao = db.tagDao()

    @Provides
    @Singleton
    fun provideTrnDao(db: IvyWalletDb): TrnDao = db.trnDao()

    @Provides
    @Singleton
    fun provideTrnLinkRecordDao(db: IvyWalletDb): TrnLinkRecordDao = db.trnLinkRecordDao()

    @Provides
    @Singleton
    fun provideTrnMetadataDao(db: IvyWalletDb): TrnMetadataDao = db.trnMetadataDao()

    @Provides
    @Singleton
    fun provideTrnTagDao(db: IvyWalletDb): TrnTagDao = db.trnTagDao()

    @Provides
    @Singleton
    fun provideAttachmentDao(db: IvyWalletDb): AttachmentDao = db.attachmentDao()
}