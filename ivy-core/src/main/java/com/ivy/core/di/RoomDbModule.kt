package com.ivy.core.di

import android.content.Context
import com.ivy.core.data.db.IvyRoomDatabase
import com.ivy.core.data.db.dao.AccountDao
import com.ivy.core.data.db.dao.BudgetDao
import com.ivy.core.data.db.dao.CategoryDao
import com.ivy.core.data.db.dao.ExchangeRateDao
import com.ivy.core.data.db.dao.LoanDao
import com.ivy.core.data.db.dao.LoanRecordDao
import com.ivy.core.data.db.dao.PlannedPaymentRuleDao
import com.ivy.core.data.db.dao.SettingsDao
import com.ivy.core.data.db.dao.TransactionDao
import com.ivy.core.data.db.dao.UserDao
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
        return db.userDao()
    }

    @Provides
    fun provideAccountDao(db: IvyRoomDatabase): AccountDao {
        return db.accountDao()
    }

    @Provides
    fun provideTransactionDao(db: IvyRoomDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    fun provideCategoryDao(db: IvyRoomDatabase): CategoryDao {
        return db.categoryDao()
    }

    @Provides
    fun provideBudgetDao(db: IvyRoomDatabase): BudgetDao {
        return db.budgetDao()
    }

    @Provides
    fun provideSettingsDao(db: IvyRoomDatabase): SettingsDao {
        return db.settingsDao()
    }

    @Provides
    fun provideLoanDao(db: IvyRoomDatabase): LoanDao {
        return db.loanDao()
    }

    @Provides
    fun provideLoanRecordDao(db: IvyRoomDatabase): LoanRecordDao {
        return db.loanRecordDao()
    }

    @Provides
    fun provideTrnRecurringRuleDao(db: IvyRoomDatabase): PlannedPaymentRuleDao {
        return db.plannedPaymentRuleDao()
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: IvyRoomDatabase
    ): ExchangeRateDao {
        return roomDatabase.exchangeRatesDao()
    }
}