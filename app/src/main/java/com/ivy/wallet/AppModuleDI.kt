package com.ivy.wallet

import android.content.Context
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.domain.deprecated.logic.*
import com.ivy.wallet.domain.deprecated.logic.csv.*
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LTLoanMapper
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LTLoanRecordMapper
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsCore
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.ui.IvyWalletCtx
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleDI {
    @Provides
    @Singleton
    fun provideIvyContext(): IvyWalletCtx {
        return IvyWalletCtx()
    }

    @Provides
    @Singleton
    fun provideNavigation(): Navigation {
        return Navigation()
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(
        @ApplicationContext appContext: Context,
    ): SharedPrefs {
        return SharedPrefs(appContext)
    }

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
    fun provideUserDao(db: IvyRoomDatabase): UserDao = db.userDao()

    @Provides
    fun provideAccountDao(db: IvyRoomDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideTransactionDao(db: IvyRoomDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: IvyRoomDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideBudgetDao(db: IvyRoomDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideSettingsDao(db: IvyRoomDatabase): SettingsDao = db.settingsDao()

    @Provides
    fun provideLoanDao(db: IvyRoomDatabase): LoanDao = db.loanDao()

    @Provides
    fun provideLoanRecordDao(db: IvyRoomDatabase): LoanRecordDao = db.loanRecordDao()

    @Provides
    fun provideTrnRecurringRuleDao(db: IvyRoomDatabase): PlannedPaymentRuleDao =
        db.plannedPaymentRuleDao()

    @Provides
    fun provideWalletAccountLogic(
        transactionDao: TransactionDao,
        exchangeRatesLogic: ExchangeRatesLogic,
        accountDao: AccountDao,
        settingsDao: SettingsDao
    ): WalletAccountLogic = WalletAccountLogic(
        transactionDao = transactionDao,
        exchangeRatesLogic = exchangeRatesLogic,
        accountDao = accountDao,
        settingsDao = settingsDao
    )

    @Provides
    fun provideWalletCategoryLogic(
        accountDao: AccountDao,
        settingsDao: SettingsDao,
        exchangeRatesLogic: ExchangeRatesLogic,
        transactionDao: TransactionDao
    ): WalletCategoryLogic = WalletCategoryLogic(
        accountDao = accountDao,
        settingsDao = settingsDao,
        exchangeRatesLogic = exchangeRatesLogic,
        transactionDao = transactionDao
    )

    @Provides
    fun providePreloadDataLogic(
        accountDao: AccountDao,
        categoryDao: CategoryDao
    ): PreloadDataLogic {
        return PreloadDataLogic(
            accountsDao = accountDao,
            categoryDao = categoryDao
        )
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: IvyRoomDatabase
    ): ExchangeRateDao {
        return roomDatabase.exchangeRatesDao()
    }

    @Provides
    fun provideCategoryCreator(
        paywallLogic: PaywallLogic,
        categoryDao: CategoryDao,
    ): CategoryCreator {
        return CategoryCreator(
            paywallLogic = paywallLogic,
            categoryDao = categoryDao,
        )
    }

    @Provides
    fun provideBudgetCreator(
        paywallLogic: PaywallLogic,
        budgetDao: BudgetDao,
    ): BudgetCreator {
        return BudgetCreator(
            paywallLogic = paywallLogic,
            budgetDao = budgetDao,
        )
    }

    @Provides
    fun provideLoanCreator(
        paywallLogic: PaywallLogic,
        dao: LoanDao,
    ): LoanCreator {
        return LoanCreator(
            paywallLogic = paywallLogic,
            dao = dao,
        )
    }

    @Provides
    fun provideLoanRecordCreator(
        paywallLogic: PaywallLogic,
        dao: LoanRecordDao,
    ): LoanRecordCreator {
        return LoanRecordCreator(
            paywallLogic = paywallLogic,
            dao = dao,
        )
    }

    @Provides
    fun provideAccountCreator(
        paywallLogic: PaywallLogic,
        accountDao: AccountDao,
        accountLogic: WalletAccountLogic,
    ): AccountCreator {
        return AccountCreator(
            paywallLogic = paywallLogic,
            accountDao = accountDao,
            accountLogic = accountLogic,
        )
    }

    @Provides
    fun provideLogoutLogic(
        ivyRoomDatabase: IvyRoomDatabase,
        sharedPrefs: SharedPrefs,
        navigation: Navigation
    ): LogoutLogic {
        return LogoutLogic(
            ivyDb = ivyRoomDatabase,
            sharedPrefs = sharedPrefs,
            navigation = navigation
        )
    }

    @Provides
    fun provideCustomerJourneyLogic(
        transactionDao: TransactionDao,
        plannedPaymentRuleDao: PlannedPaymentRuleDao,
        sharedPrefs: SharedPrefs,
        ivyContext: IvyWalletCtx
    ): CustomerJourneyLogic {
        return CustomerJourneyLogic(
            transactionDao = transactionDao,
            plannedPaymentRuleDao = plannedPaymentRuleDao,
            sharedPrefs = sharedPrefs,
            ivyContext = ivyContext
        )
    }

    @Provides
    fun provideSmartTitleSuggestionsLogic(
        transactionDao: TransactionDao
    ): SmartTitleSuggestionsLogic {
        return SmartTitleSuggestionsLogic(
            transactionDao = transactionDao
        )
    }

    @Provides
    fun provideWalletDAOs(
        accountDao: AccountDao,
        transactionDao: TransactionDao,
        exchangeRateDao: ExchangeRateDao
    ): WalletDAOs {
        return WalletDAOs(
            accountDao = accountDao,
            transactionDao = transactionDao,
            exchangeRateDao = exchangeRateDao
        )
    }

    @Provides
    fun loanTransactionsLogic(loanTransactionsCore: LoanTransactionsCore): LoanTransactionsLogic {
        return LoanTransactionsLogic(
            Loan = LTLoanMapper(ltCore = loanTransactionsCore),
            LoanRecord = LTLoanRecordMapper(ltCore = loanTransactionsCore)
        )
    }
}
