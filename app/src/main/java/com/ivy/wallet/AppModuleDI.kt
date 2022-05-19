package com.ivy.wallet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.android.billing.IvyBilling
import com.ivy.wallet.android.notification.NotificationService
import com.ivy.wallet.domain.deprecated.logic.*
import com.ivy.wallet.domain.deprecated.logic.csv.*
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LTLoanMapper
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LTLoanRecordMapper
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsCore
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import com.ivy.wallet.domain.deprecated.logic.zip.ExportZipLogic
import com.ivy.wallet.domain.deprecated.sync.IvySync
import com.ivy.wallet.domain.deprecated.sync.item.*
import com.ivy.wallet.domain.deprecated.sync.uploader.*
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.network.*
import com.ivy.wallet.io.network.error.ErrorCode
import com.ivy.wallet.io.network.error.NetworkError
import com.ivy.wallet.io.network.error.RestError
import com.ivy.wallet.io.network.service.ExpImagesService
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.ui.IvyWalletCtx
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import javax.inject.Singleton
import kotlin.random.Random

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
        navigation: Navigation,
        accountDao: AccountDao,
        categoryDao: CategoryDao,
        budgetDao: BudgetDao,
        loanDao: LoanDao
    ): PaywallLogic {
        return PaywallLogic(
            ivyBilling = ivyBilling,
            ivyContext = ivyContext,
            navigation = navigation,
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
        @ApplicationContext appContext: Context,
        sharedPrefs: SharedPrefs,
    ): TransactionReminderLogic {
        return TransactionReminderLogic(
            appContext = appContext,
            sharedPrefs = sharedPrefs
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
        navigation: Navigation
    ): LogoutLogic {
        return LogoutLogic(
            ivyDb = ivyRoomDatabase,
            ivySession = ivySession,
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
    @Singleton
    fun loanTransactionsCore(
        categoryDao: CategoryDao,
        transactionUploader: TransactionUploader,
        transactionDao: TransactionDao,
        ivyContext: IvyWalletCtx,
        loanDao: LoanDao,
        loanRecordDao: LoanRecordDao,
        exchangeRatesLogic: ExchangeRatesLogic,
        settingsDao: SettingsDao,
        accountDao: AccountDao
    ): LoanTransactionsCore {
        return LoanTransactionsCore(
            categoryDao = categoryDao,
            transactionUploader = transactionUploader,
            transactionDao = transactionDao,
            ivyContext = ivyContext,
            loanDao = loanDao,
            loanRecordDao = loanRecordDao,
            settingsDao = settingsDao,
            accountsDao = accountDao,
            exchangeRatesLogic = exchangeRatesLogic
        )
    }

    @Provides
    fun loanTransactionsLogic(loanTransactionsCore: LoanTransactionsCore): LoanTransactionsLogic {
        return LoanTransactionsLogic(
            Loan = LTLoanMapper(ltCore = loanTransactionsCore),
            LoanRecord = LTLoanRecordMapper(ltCore = loanTransactionsCore)
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
    fun provideExpImagesService(): ExpImagesService = object : ExpImagesService {
        override suspend fun fetchImages(): List<String> {
            val randDelay = Random.nextLong(from = 300, until = 1500)
            delay(randDelay)

            val success = Random.nextBoolean()
            return if (success) {
                val res = mutableListOf<String>()

                val images = listOf(
                    "https://stimg.cardekho.com/images/carexteriorimages/930x620/Lamborghini/Aventador/6721/Lamborghini-Aventador-SVJ/1621849426405/front-left-side-47.jpg",
                    "https://scuffedentertainment.com/wp-content/uploads/2021/11/what-car-suits-you-best-quiz.jpg",
                    "malformed_url",
                    "https://maserati.scene7.com/is/image/maserati/maserati/regional/us/models/my22/levante/22_LV_Trofeo_PS_T1_HomePage_1920x1080.jpg?\$1920x2000\$&fit=constrain",
                    "https://i.ytimg.com/vi/dip_8dmrcaU/maxresdefault.jpg",
                    "https://img.poki.com/cdn-cgi/image/quality=78,width=600,height=600,fit=cover,f=auto/94945631828bfdcf32a8ad0b79978913.png",
                    "https://pixelmedia.bg/wp-content/uploads/2021/08/Apple-Car.jpeg",
                    "https://www.teslarati.com/wp-content/uploads/2021/12/apple-car-patent.jpeg"
                )

                for (i in 0..50) {
                    res.addAll(images)
                }

                res
            } else {
                throw NetworkError(
                    restError = RestError(ErrorCode.UNKNOWN, "Random error")
                )
            }
        }

    }
}