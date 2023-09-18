package com.ivy.core.di

import android.content.Context
import com.ivy.core.db.IvyRoomDatabase
import com.ivy.core.db.read.AccountDao
import com.ivy.core.db.read.BudgetDao
import com.ivy.core.db.read.CategoryDao
import com.ivy.core.db.read.ExchangeRatesDao
import com.ivy.core.db.read.LoanDao
import com.ivy.core.db.read.LoanRecordDao
import com.ivy.core.db.read.PlannedPaymentRuleDao
import com.ivy.core.db.read.SettingsDao
import com.ivy.core.db.read.TransactionDao
import com.ivy.core.db.read.UserDao
import com.ivy.core.db.write.dao.WriteAccountDao
import com.ivy.core.db.write.dao.WriteBudgetDao
import com.ivy.core.db.write.dao.WriteCategoryDao
import com.ivy.core.db.write.dao.WriteExchangeRatesDao
import com.ivy.core.db.write.dao.WriteLoanDao
import com.ivy.core.db.write.dao.WriteLoanRecordDao
import com.ivy.core.db.write.dao.WritePlannedPaymentRuleDao
import com.ivy.core.db.write.dao.WriteSettingsDao
import com.ivy.core.db.write.dao.WriteTransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDbModule {

    @Provides
    @Singleton
    fun provideIvyRoomDatabase(
        @ApplicationContext appContext: Context,
    ): IvyRoomDatabase {
        return IvyRoomDatabase.create(
            applicationContext = appContext,
        )
    }

    @Provides
    fun provideUserDao(db: IvyRoomDatabase): UserDao {
        return db.userDao
    }

    @Provides
    fun provideAccountDao(db: IvyRoomDatabase): AccountDao {
        return db.accountDao
    }

    @Provides
    fun provideTransactionDao(db: IvyRoomDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    fun provideCategoryDao(db: IvyRoomDatabase): CategoryDao {
        return db.categoryDao
    }

    @Provides
    fun provideBudgetDao(db: IvyRoomDatabase): BudgetDao {
        return db.budgetDao
    }

    @Provides
    fun provideSettingsDao(db: IvyRoomDatabase): SettingsDao {
        return db.settingsDao
    }

    @Provides
    fun provideLoanDao(db: IvyRoomDatabase): LoanDao {
        return db.loanDao
    }

    @Provides
    fun provideLoanRecordDao(db: IvyRoomDatabase): LoanRecordDao {
        return db.loanRecordDao
    }

    @Provides
    fun providePlannedPaymentRuleDao(db: IvyRoomDatabase): PlannedPaymentRuleDao {
        return db.plannedPaymentRuleDao
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: IvyRoomDatabase
    ): ExchangeRatesDao {
        return roomDatabase.exchangeRatesDao
    }

    @Provides
    fun provideWriteAccountDao(db: IvyRoomDatabase): WriteAccountDao {
        return db.writeAccountDao
    }

    @Provides
    fun provideWriteTransactionDao(db: IvyRoomDatabase): WriteTransactionDao {
        return db.writeTransactionDao
    }

    @Provides
    fun provideWriteCategoryDao(db: IvyRoomDatabase): WriteCategoryDao {
        return db.writeCategoryDao
    }

    @Provides
    fun provideWriteBudgetDao(db: IvyRoomDatabase): WriteBudgetDao {
        return db.writeBudgetDao
    }

    @Provides
    fun provideWriteSettingsDao(db: IvyRoomDatabase): WriteSettingsDao {
        return db.writeSettingsDao
    }

    @Provides
    fun provideWriteLoanDao(db: IvyRoomDatabase): WriteLoanDao {
        return db.writeLoanDao
    }

    @Provides
    fun provideWriteLoanRecordDao(db: IvyRoomDatabase): WriteLoanRecordDao {
        return db.writeLoanRecordDao
    }

    @Provides
    fun provideWritePlannedPaymentRuleDao(db: IvyRoomDatabase): WritePlannedPaymentRuleDao {
        return db.writePlannedPaymentRuleDao
    }

    @Provides
    fun provideWriteExchangeRatesDao(db: IvyRoomDatabase): WriteExchangeRatesDao {
        return db.writeExchangeRatesDao
    }
}