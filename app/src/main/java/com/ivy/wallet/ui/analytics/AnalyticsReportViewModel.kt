package com.ivy.wallet.ui.analytics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.data.analytics.AnalyticsEvent
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.ui.analytics.model.Health
import com.ivy.wallet.ui.analytics.model.KPIs
import com.ivy.wallet.ui.analytics.model.OnboardingReport
import com.ivy.wallet.ui.analytics.model.UserStats
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.toEpochSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AnalyticsReportViewModel @Inject constructor(
    private val restClient: RestClient
) : ViewModel() {

    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate = _startDate.asLiveData()

    private val _endDate = MutableLiveData<LocalDateTime?>()
    val endDate = _endDate.asLiveData()

    private val _selectedTab = MutableLiveData(AnalyticsTab.KPIs)
    val selectedTab = _selectedTab.asLiveData()

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading.asLiveData()

    private val _kpis = MutableLiveData<KPIs>()
    val kpis = _kpis.asLiveData()

    private val _onboardingReport = MutableLiveData<OnboardingReport>()
    val onboardingReport = _onboardingReport.asLiveData()

    private val _health = MutableLiveData<Health>()
    val health = _health.asLiveData()

    private val _userStats = MutableLiveData<UserStats>()
    val userStats = _userStats.asLiveData()

    fun start(
        startDate: LocalDateTime? = this.startDate.value,
        endDate: LocalDateTime? = this.endDate.value,
    ) {
        viewModelScope.launch {
            try {
                _startDate.value = startDate
                _endDate.value = endDate

                _loading.value = true

                val report = ioThread {
                    restClient.analyticsService.getReport(
                        startDate = startDate?.toEpochSeconds(),
                        endDate = endDate?.toEpochSeconds()
                    )
                }

                val events = report.eventsCount
                _onboardingReport.value = OnboardingReport(
                    onboardingStarted = events[AnalyticsEvent.ONBOARDING_STARTED],
                    avgOnboardingStarted = report.eventAvgCount?.get(AnalyticsEvent.ONBOARDING_STARTED),

                    onboardingLogin = events[AnalyticsEvent.ONBOARDING_LOGIN],
                    onboardingPrivacyTCAccepted = events[AnalyticsEvent.ONBOARDING_PRIVACY_TC_ACCEPTED],
                    onboardingLocalAccount = events[AnalyticsEvent.ONBOARDING_LOCAL_ACCOUNT],
                    onboardingSetName = events[AnalyticsEvent.ONBOARDING_NAME_SET],
                    onboardingSetCurrency = events[AnalyticsEvent.ONBOARDING_CURRENCY_SET],
                    onboardingAddAccount = events[AnalyticsEvent.ONBOARDING_ACCOUNTS_DONE],
                    onboardingCompleted = events[AnalyticsEvent.ONBOARDING_COMPLETED]
                )

                KPIs(
                    onboardedUsers = report.onboardedUsersCount,

                    usersCreatedAccount = report.usersCreatedAccount.size,
                    avgAccountsPerUser = avg(report.usersCreatedAccount),

                    usersCreatedCategory = report.usersCreatedCategory.size,
                    avgCategoriesPerUser = avg(report.usersCreatedCategory),

                    usersCreatedTransaction = report.usersCreatedTransaction.size,
                    avgTransactionsPerUser = avg(report.usersCreatedTransaction),

                    usersCreatedPlannedPayment = report.usersCreatedPlannedPayment.size,
                    avgPlannedPaymentsPerUser = avg(report.usersCreatedPlannedPayment),

                    paywallAccounts = report.eventsCount[AnalyticsEvent.PAYWALL_ACCOUNTS] ?: 0,
                    avgPaywallAccounts = report.eventAvgCount[AnalyticsEvent.PAYWALL_ACCOUNTS],

                    paywallCategories = report.eventsCount[AnalyticsEvent.PAYWALL_CATEGORIES] ?: 0,
                    avgPaywallCategories = report.eventAvgCount[AnalyticsEvent.PAYWALL_CATEGORIES],

                    paywallNoReason = report.eventsCount[AnalyticsEvent.PAYWALL_NO_REASON] ?: 0,
                    avgPaywallNoReason = report.eventAvgCount[AnalyticsEvent.PAYWALL_NO_REASON],

                    paywallExportCSV = report.eventsCount[AnalyticsEvent.PAYWALL_EXPORT_CSV] ?: 0,
                    avgPaywallExportCSV = report.eventAvgCount[AnalyticsEvent.PAYWALL_EXPORT_CSV],

                    paywallPremiumColor = report.eventsCount[AnalyticsEvent.PAYWALL_PREMIUM_COLOR]
                        ?: 0,
                    avgPaywallPremiumColor = report.eventAvgCount[AnalyticsEvent.PAYWALL_PREMIUM_COLOR],

                    choosePlan = report.eventsCount[AnalyticsEvent.PAYWALL_CHOOSE_PLAN] ?: 0,
                    choosePlanMonthly = report.eventsCount[AnalyticsEvent.PAYWALL_CHOOSE_PLAN_MONTHLY]
                        ?: 0,
                    choosePlan6Month = report.eventsCount[AnalyticsEvent.PAYWALL_CHOOSE_PLAN_6MONTH]
                        ?: 0,
                    choosePlanYearly = report.eventsCount[AnalyticsEvent.PAYWALL_CHOOSE_PLAN_YEARLY]
                        ?: 0,
                    choosePlanLifetime = report.eventsCount[AnalyticsEvent.PAYWALL_CHOOSE_PLAN_LIFETIME]
                        ?: 0,

                    startBuyPremium = report.eventsCount[AnalyticsEvent.PAYWALL_START_BUY] ?: 0,
                    startBuyPremiumMonthly = report.eventsCount[AnalyticsEvent.PAYWALL_START_BUY_MONTHLY]
                        ?: 0,
                    startBuyPremium6Month = report.eventsCount[AnalyticsEvent.PAYWALL_START_BUY_6MONTH]
                        ?: 0,
                    startBuyPremiumYearly = report.eventsCount[AnalyticsEvent.PAYWALL_START_BUY_YEARLY]
                        ?: 0,
                    startBuyPremiumLifetime = report.eventsCount[AnalyticsEvent.PAYWALL_START_BUY_LIFETIME]
                        ?: 0,

                    activePremium = report.eventsCount[AnalyticsEvent.PAYWALL_ACTIVE_PREMIUM] ?: 0,
                ).also { _kpis.value = it }

                _health.value = Health(
                    usersWithTransactionLast24h = report.usersWithTransactionLast24H.size,
                    avgTransactionsLast24 = avg(report.usersWithTransactionLast24H),

                    usersWithTransactionLast7Days = report.usersWithTransactionLast7Days.size,
                    avgTransactionsLast7Days = avg(report.usersWithTransactionLast7Days),

                    usersWithTransactionLast30Days = report.usersWithTransactionLast30Days.size,
                    avgTransactionsLast30Days = avg(report.usersWithTransactionLast30Days),

                    loggedFromSettings = report.eventsCount[AnalyticsEvent.LOGIN_FROM_SETTINGS] ?: 0
                )

                _userStats.value = UserStats(
                    powerUsers = report.powerUsersV1.size,
                    avgPowerUsersTrns = avg(report.powerUsersV1),

                    activeUsers = report.activeUsersV1.size,
                    avgActiveUsersTrns = avg(report.activeUsersV1),

                    dyingUsers = report.dyingUsersV1.size,
                    avgDyingUsers = avg(report.dyingUsersV1)
                )

                _loading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
            }
        }
    }

    fun selectTab(tab: AnalyticsTab) {
        _selectedTab.value = tab
    }

    fun setStartDate(startDate: LocalDateTime?) {
        start(
            startDate = startDate,
            endDate = endDate.value
        )
    }

    fun setEndDate(endDate: LocalDateTime?) {
        start(
            startDate = startDate.value,
            endDate = endDate
        )
    }

    fun nukeTestEvents() {
        viewModelScope.launch {
            ioThread {
                restClient.analyticsService.nukeTestEvents()
            }

            start()
        }
    }

    private fun avg(list: List<Int>): Double = list
        .takeIf {
            list.isNotEmpty()
        }
        ?.sum()
        ?.div(list.size.toDouble()) ?: 0.0
}