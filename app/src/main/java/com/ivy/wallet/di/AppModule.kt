package com.ivy.wallet.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ivy.wallet.analytics.IvyAnalytics
import com.ivy.wallet.billing.IvyBilling
import com.ivy.wallet.functional.data.WalletDAOs
import com.ivy.wallet.logic.*
import com.ivy.wallet.logic.bankintegrations.BankIntegrationsLogic
import com.ivy.wallet.logic.bankintegrations.SaltEdgeAccountMapper
import com.ivy.wallet.logic.bankintegrations.SaltEdgeCategoryMapper
import com.ivy.wallet.logic.bankintegrations.SaltEdgeTransactionMapper
import com.ivy.wallet.logic.csv.*
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.notification.TransactionReminderLogic
import com.ivy.wallet.network.ErrorCodeTypeAdapter
import com.ivy.wallet.network.FCMClient
import com.ivy.wallet.network.LocalDateTimeTypeAdapter
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.error.ErrorCode
import com.ivy.wallet.persistence.IvyRoomDatabase
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.sync.IvySync
import com.ivy.wallet.sync.item.*
import com.ivy.wallet.sync.uploader.*
import com.ivy.wallet.system.notification.NotificationService
import com.ivy.wallet.ui.IvyWalletCtx
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
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
    fun provideFCMClient(
        sharedPrefs: SharedPrefs
    ): FCMClient {
        return FCMClient(sharedPrefs)
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
    fun provideWishlistItemDao(db: IvyRoomDatabase): WishlistItemDao = db.wishlistItemDao()

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
    fun provideWalletLogic(
        accountDao: AccountDao,
        transactionDao: TransactionDao,
        settingsDao: SettingsDao,
        exchangeRatesLogic: ExchangeRatesLogic,
    ): WalletLogic = WalletLogic(
        accountDao = accountDao,
        transactionDao = transactionDao,
        settingsDao = settingsDao,
        exchangeRatesLogic = exchangeRatesLogic,
    )

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
    fun provideRecurringGenerator(
        transactionDao: TransactionDao,
    ): PlannedPaymentsGenerator = PlannedPaymentsGenerator(
        transactionDao = transactionDao,
    )

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
    fun providePlannedPaymentsLogic(
        plannedPaymentRuleDao: PlannedPaymentRuleDao,
        transactionDao: TransactionDao,
        transactionUploader: TransactionUploader,
        exchangeRatesLogic: ExchangeRatesLogic,
        accountDao: AccountDao,
        settingsDao: SettingsDao,
        plannedPaymentRuleUploader: PlannedPaymentRuleUploader
    ): PlannedPaymentsLogic {
        return PlannedPaymentsLogic(
            plannedPaymentRuleDao = plannedPaymentRuleDao,
            transactionDao = transactionDao,
            transactionUploader = transactionUploader,
            accountDao = accountDao,
            exchangeRatesLogic = exchangeRatesLogic,
            settingsDao = settingsDao,
            plannedPaymentRuleUploader = plannedPaymentRuleUploader
        )
    }

    @Provides
    @Singleton
    fun provideIvyBilling(
    ): IvyBilling {
        return IvyBilling()
    }

    @Provides
    @Singleton
    fun providepaywallLogic(
        ivyBilling: IvyBilling,
        ivyContext: IvyWalletCtx,
        accountDao: AccountDao,
        categoryDao: CategoryDao,
        budgetDao: BudgetDao,
        loanDao: LoanDao
    ): PaywallLogic {
        return PaywallLogic(
            ivyBilling = ivyBilling,
            ivyContext = ivyContext,
            accountDao = accountDao,
            categoryDao = categoryDao,
            budgetDao = budgetDao,
            loanDao = loanDao
        )
    }

    @Provides
    @Singleton
    fun provideIvyAnalytics(
        sharedPrefs: SharedPrefs,
        restClient: RestClient
    ): IvyAnalytics {
        return IvyAnalytics(
            sharedPrefs = sharedPrefs,
            restClient = restClient
        )
    }

    @Provides
    fun provideExportCSVLogic(
        settingsDao: SettingsDao,
        transactionDao: TransactionDao,
        categoryDao: CategoryDao,
        accountDao: AccountDao
    ): ExportCSVLogic {
        return ExportCSVLogic(
            settingsDao = settingsDao,
            transactionDao = transactionDao,
            categoryDao = categoryDao,
            accountDao = accountDao
        )
    }

    @Provides
    fun provideNotificationService(
        @ApplicationContext appContext: Context
    ): NotificationService {
        return NotificationService(appContext)
    }

    @Provides
    fun provideTransactionReminderLogic(
        @ApplicationContext appContext: Context
    ): TransactionReminderLogic {
        return TransactionReminderLogic(
            appContext = appContext
        )
    }

    @Provides
    fun provideFileReader(
        @ApplicationContext appContext: Context
    ): IvyFileReader {
        return IvyFileReader(
            appContext = appContext
        )
    }

    @Provides
    fun provideCSMNormalizer(): CSVNormalizer {
        return CSVNormalizer()
    }

    @Provides
    fun provideCSVMapper(): CSVMapper {
        return CSVMapper()
    }

    @Provides
    fun provideCSMImporter(
        settingsDao: SettingsDao,
        accountDao: AccountDao,
        categoryDao: CategoryDao,
        transactionDao: TransactionDao
    ): CSVImporter {
        return CSVImporter(
            settingsDao = settingsDao,
            accountDao = accountDao,
            categoryDao = categoryDao,
            transactionDao = transactionDao
        )
    }

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
    fun provideExchangeRatesLogic(
        restClient: RestClient,
        exchangeRateDao: ExchangeRateDao
    ): ExchangeRatesLogic {
        return ExchangeRatesLogic(
            restClient = restClient,
            exchangeRateDao = exchangeRateDao
        )
    }

    @Provides
    fun provideCategoryCreator(
        paywallLogic: PaywallLogic,
        categoryDao: CategoryDao,
        categoryUploader: CategoryUploader
    ): CategoryCreator {
        return CategoryCreator(
            paywallLogic = paywallLogic,
            categoryDao = categoryDao,
            categoryUploader = categoryUploader
        )
    }

    @Provides
    fun provideBudgetCreator(
        paywallLogic: PaywallLogic,
        budgetDao: BudgetDao,
        budgetUploader: BudgetUploader
    ): BudgetCreator {
        return BudgetCreator(
            paywallLogic = paywallLogic,
            budgetDao = budgetDao,
            budgetUploader = budgetUploader
        )
    }

    @Provides
    fun provideLoanCreator(
        paywallLogic: PaywallLogic,
        dao: LoanDao,
        uploader: LoanUploader
    ): LoanCreator {
        return LoanCreator(
            paywallLogic = paywallLogic,
            dao = dao,
            uploader = uploader
        )
    }

    @Provides
    fun provideLoanRecordCreator(
        paywallLogic: PaywallLogic,
        dao: LoanRecordDao,
        uploader: LoanRecordUploader
    ): LoanRecordCreator {
        return LoanRecordCreator(
            paywallLogic = paywallLogic,
            dao = dao,
            uploader = uploader
        )
    }

    @Provides
    fun provideAccountCreator(
        paywallLogic: PaywallLogic,
        accountDao: AccountDao,
        accountUploader: AccountUploader,
        accountLogic: WalletAccountLogic,
        transactionSync: TransactionSync
    ): AccountCreator {
        return AccountCreator(
            paywallLogic = paywallLogic,
            accountDao = accountDao,
            transactionSync = transactionSync,
            accountLogic = accountLogic,
            accountUploader = accountUploader,
        )
    }

    @Provides
    fun provideLogoutLogic(
        ivyRoomDatabase: IvyRoomDatabase,
        ivySession: IvySession,
        sharedPrefs: SharedPrefs,
        ivyContext: IvyWalletCtx
    ): LogoutLogic {
        return LogoutLogic(
            ivyDb = ivyRoomDatabase,
            ivySession = ivySession,
            sharedPrefs = sharedPrefs,
            ivyContext = ivyContext
        )
    }

    @Provides
    fun provideSaltEdgeLogic(
        restClient: RestClient,
        seTransactionsMapper: SaltEdgeTransactionMapper,
        ivySession: IvySession,
        sharedPrefs: SharedPrefs
    ): BankIntegrationsLogic {
        return BankIntegrationsLogic(
            restClient = restClient,
            seTransactionMapper = seTransactionsMapper,
            ivySession = ivySession,
            sharedPrefs = sharedPrefs
        )
    }

    @Provides
    fun provideSeTransactionMapper(
        transactionDao: TransactionDao,
        seAccountMapper: SaltEdgeAccountMapper,
        seCategoryMapper: SaltEdgeCategoryMapper,
        accountDao: AccountDao,
        walletAccountLogic: WalletAccountLogic
    ): SaltEdgeTransactionMapper {
        return SaltEdgeTransactionMapper(
            transactionDao = transactionDao,
            seAccountMapper = seAccountMapper,
            seCategoryMapper = seCategoryMapper,
            accountDao = accountDao,
            walletAccountLogic = walletAccountLogic
        )
    }

    @Provides
    fun provideSeAccountMapper(
        accountDao: AccountDao
    ): SaltEdgeAccountMapper {
        return SaltEdgeAccountMapper(
            accountDao = accountDao
        )
    }

    @Provides
    fun provideSeCategoryMapper(
        categoryDao: CategoryDao
    ): SaltEdgeCategoryMapper {
        return SaltEdgeCategoryMapper(
            categoryDao = categoryDao
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
}