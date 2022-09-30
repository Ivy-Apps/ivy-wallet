package com.ivy.wallet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ivy.billing.IvyBilling
import com.ivy.core.ui.temp.trash.IvyWalletCtx
import com.ivy.notifications.NotificationService
import com.ivy.temp.persistence.ExchangeRateDao
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import com.ivy.wallet.domain.deprecated.logic.zip.ExportZipLogic
import com.ivy.wallet.domain.deprecated.sync.IvySync
import com.ivy.wallet.domain.deprecated.sync.item.*
import com.ivy.wallet.domain.deprecated.sync.uploader.*
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.network.ErrorCodeTypeAdapter
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.LocalDateTimeTypeAdapter
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.error.ErrorCode
import com.ivy.wallet.io.network.service.AccountService
import com.ivy.wallet.io.network.service.CategoryService
import com.ivy.wallet.io.network.service.TransactionService
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
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
    fun provideSharedPrefs(
        @ApplicationContext appContext: Context,
    ): SharedPrefs {
        return SharedPrefs(appContext)
    }

    @Provides
    @Singleton
    fun provideIvySession(sharedPrefs: SharedPrefs, userDao: UserDao): IvySession {
        return IvySession(sharedPrefs, userDao)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ErrorCode::class.java, ErrorCodeTypeAdapter())
            .create()
    }

    @Provides
    @Singleton
    fun provideRestClient(
        @ApplicationContext appContext: Context,
        gson: Gson,
        ivySession: IvySession
    ): RestClient {
        return RestClient.initialize(appContext, ivySession, gson)
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

    //Sync
    @Provides
    fun provideAccountUploader(
        accountDao: AccountDao,
        transactionDao: TransactionDao,
        restClient: RestClient,
        ivySession: IvySession
    ): AccountUploader {
        return AccountUploader(
            accountDao = accountDao,
            transactionDao = transactionDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideAccountSync(
        sharedPrefs: SharedPrefs,
        dao: AccountDao,
        restClient: RestClient,
        uploader: AccountUploader,
        ivySession: IvySession
    ): AccountSync {
        return AccountSync(
            sharedPrefs = sharedPrefs,
            dao = dao,
            restClient = restClient,
            uploader = uploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideCategoryUploader(
        categoryDao: CategoryDao,
        restClient: RestClient,
        ivySession: IvySession
    ): CategoryUploader {
        return CategoryUploader(
            dao = categoryDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideBudgetUploader(
        budgetDao: BudgetDao,
        restClient: RestClient,
        ivySession: IvySession
    ): BudgetUploader {
        return BudgetUploader(
            dao = budgetDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideLoanUploader(
        loanDao: LoanDao,
        restClient: RestClient,
        ivySession: IvySession
    ): LoanUploader {
        return LoanUploader(
            dao = loanDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideLoanRecordUploader(
        dao: LoanRecordDao,
        restClient: RestClient,
        ivySession: IvySession
    ): LoanRecordUploader {
        return LoanRecordUploader(
            dao = dao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideCategorySync(
        sharedPrefs: SharedPrefs,
        categoryDao: CategoryDao,
        restClient: RestClient,
        categoryUploader: CategoryUploader,
        ivySession: IvySession
    ): CategorySync {
        return CategorySync(
            sharedPrefs = sharedPrefs,
            dao = categoryDao,
            restClient = restClient,
            uploader = categoryUploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideBudgetSync(
        sharedPrefs: SharedPrefs,
        budgetDao: BudgetDao,
        restClient: RestClient,
        budgetUploader: BudgetUploader,
        ivySession: IvySession
    ): BudgetSync {
        return BudgetSync(
            sharedPrefs = sharedPrefs,
            dao = budgetDao,
            restClient = restClient,
            uploader = budgetUploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideLoanSync(
        sharedPrefs: SharedPrefs,
        dao: LoanDao,
        restClient: RestClient,
        loanUploader: LoanUploader,
        ivySession: IvySession
    ): LoanSync {
        return LoanSync(
            sharedPrefs = sharedPrefs,
            dao = dao,
            restClient = restClient,
            uploader = loanUploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideLoanRecordSync(
        sharedPrefs: SharedPrefs,
        dao: LoanRecordDao,
        restClient: RestClient,
        uploader: LoanRecordUploader,
        ivySession: IvySession
    ): LoanRecordSync {
        return LoanRecordSync(
            sharedPrefs = sharedPrefs,
            dao = dao,
            restClient = restClient,
            uploader = uploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideTransactionUploader(
        transactionDao: TransactionDao,
        restClient: RestClient,
        ivySession: IvySession
    ): TransactionUploader {
        return TransactionUploader(
            dao = transactionDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun provideTransactionSync(
        sharedPrefs: SharedPrefs,
        transactionDao: TransactionDao,
        restClient: RestClient,
        transactionUploader: TransactionUploader,
        ivySession: IvySession
    ): TransactionSync {
        return TransactionSync(
            sharedPrefs = sharedPrefs,
            dao = transactionDao,
            restClient = restClient,
            uploader = transactionUploader,
            ivySession = ivySession
        )
    }

    @Provides
    fun providePlannedPaymentRuleUploader(
        plannedPaymentRuleDao: PlannedPaymentRuleDao,
        restClient: RestClient,
        ivySession: IvySession
    ): PlannedPaymentRuleUploader {
        return PlannedPaymentRuleUploader(
            dao = plannedPaymentRuleDao,
            restClient = restClient,
            ivySession = ivySession
        )
    }

    @Provides
    fun providePlannedPaymentSync(
        sharedPrefs: SharedPrefs,
        plannedPaymentRuleDao: PlannedPaymentRuleDao,
        restClient: RestClient,
        plannedPaymentRuleUploader: PlannedPaymentRuleUploader,
        ivySession: IvySession
    ): PlannedPaymentSync {
        return PlannedPaymentSync(
            sharedPrefs = sharedPrefs,
            dao = plannedPaymentRuleDao,
            restClient = restClient,
            uploader = plannedPaymentRuleUploader,
            ivySession = ivySession
        )
    }

    @Provides
    @Singleton
    fun provideIvySync(
        accountSync: AccountSync,
        categorySync: CategorySync,
        transactionSync: TransactionSync,
        plannedPaymentSync: PlannedPaymentSync,
        budgetSync: BudgetSync,
        loanSync: LoanSync,
        loanRecordSync: LoanRecordSync,
        ivySession: IvySession
    ): IvySync {
        return IvySync(
            accountSync = accountSync,
            categorySync = categorySync,
            transactionSync = transactionSync,
            plannedPaymentSync = plannedPaymentSync,
            budgetSync = budgetSync,
            loanSync = loanSync,
            loanRecordSync = loanRecordSync,
            ivySession = ivySession
        )
    }

    @Provides
    @Singleton
    fun provideIvyBilling(
    ): IvyBilling {
        return IvyBilling()
    }

    @Provides
    fun provideNotificationService(
        @ApplicationContext appContext: Context
    ): NotificationService {
        return NotificationService(appContext)
    }

    @Provides
    fun provideTransactionReminderLogic(
        @ApplicationContext appContext: Context,
        sharedPrefs: SharedPrefs,
    ): TransactionReminderLogic {
        return TransactionReminderLogic(
            appContext = appContext,
            sharedPrefs = sharedPrefs
        )
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: IvyRoomDatabase
    ): ExchangeRateDao {
        return roomDatabase.exchangeRatesDao()
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
    fun providesExportZipLogic(
        accountDao: AccountDao,
        budgetDao: BudgetDao,
        categoryDao: CategoryDao,
        loanRecordDao: LoanRecordDao,
        loanDao: LoanDao,
        plannedPaymentRuleDao: PlannedPaymentRuleDao,
        settingsDao: SettingsDao,
        transactionDao: TransactionDao,
        sharedPrefs: SharedPrefs
    ): ExportZipLogic {
        return ExportZipLogic(
            accountDao,
            budgetDao,
            categoryDao,
            loanRecordDao,
            loanDao,
            plannedPaymentRuleDao,
            settingsDao,
            transactionDao,
            sharedPrefs
        )
    }

    @Provides
    fun provideAccountService(
        restClient: RestClient
    ): AccountService = restClient.accountService

    @Provides
    fun provideCategoryService(
        restClient: RestClient
    ): CategoryService = restClient.categoryService

    @Provides
    fun provideTransactionService(
        restClient: RestClient
    ): TransactionService = restClient.transactionService
}