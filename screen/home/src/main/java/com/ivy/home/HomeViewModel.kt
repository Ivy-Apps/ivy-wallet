package com.ivy.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Theme
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.domain.usecase.SyncExchangeRatesUseCase
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.base.ComposeViewModel
import com.ivy.data.repository.CategoryRepository
import com.ivy.frp.fixUnit
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.home.customerjourney.CustomerJourneyCardModel
import com.ivy.home.customerjourney.CustomerJourneyCardsProvider
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.data.BufferInfo
import com.ivy.legacy.data.LegacyDueSection
import com.ivy.legacy.data.model.MainTab
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Settings
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.domain.action.settings.UpdateSettingsAct
import com.ivy.legacy.domain.action.viewmodel.home.ShouldHideIncomeAct
import com.ivy.legacy.utils.dateNowUTC
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.BalanceScreen
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.CalcBufferDiffAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.transaction.HistoryWithDateDivsAct
import com.ivy.wallet.domain.action.viewmodel.home.HasTrnsAct
import com.ivy.wallet.domain.action.viewmodel.home.OverdueAct
import com.ivy.wallet.domain.action.viewmodel.home.ShouldHideBalanceAct
import com.ivy.wallet.domain.action.viewmodel.home.UpcomingAct
import com.ivy.wallet.domain.action.viewmodel.home.UpdateAccCacheAct
import com.ivy.wallet.domain.action.viewmodel.home.UpdateCategoriesCacheAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@Stable
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyCardsProvider,
    private val historyWithDateDivsAct: HistoryWithDateDivsAct,
    private val calcIncomeExpenseAct: CalcIncomeExpenseAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val settingsAct: SettingsAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val calcBufferDiffAct: CalcBufferDiffAct,
    private val upcomingAct: UpcomingAct,
    private val overdueAct: OverdueAct,
    private val hasTrnsAct: HasTrnsAct,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val shouldHideBalanceAct: ShouldHideBalanceAct,
    private val shouldHideIncomeAct: ShouldHideIncomeAct,
    private val updateSettingsAct: UpdateSettingsAct,
    private val updateAccCacheAct: UpdateAccCacheAct,
    private val updateCategoriesCacheAct: UpdateCategoriesCacheAct,
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase,
    private val transactionMapper: TransactionMapper
) : ComposeViewModel<HomeState, HomeEvent>() {
    private val currentTheme = mutableStateOf(Theme.AUTO)
    private val name = mutableStateOf("")
    private val period = mutableStateOf(ivyContext.selectedPeriod)
    private val baseData = mutableStateOf(
        AppBaseData(
            baseCurrency = "",
            accounts = persistentListOf(),
            categories = persistentListOf()
        )
    )
    private val history = mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val stats = mutableStateOf(IncomeExpensePair.zero())
    private val balance = mutableStateOf(BigDecimal.ZERO)
    private val buffer = mutableStateOf(
        BufferInfo(
            amount = BigDecimal.ZERO,
            bufferDiff = BigDecimal.ZERO,
        )
    )
    private val upcoming = mutableStateOf(
        LegacyDueSection(
            trns = persistentListOf(),
            stats = IncomeExpensePair.zero(),
            expanded = false,
        )
    )
    private val overdue = mutableStateOf(
        LegacyDueSection(
            trns = persistentListOf(),
            stats = IncomeExpensePair.zero(),
            expanded = false,
        )
    )
    private val customerJourneyCards =
        mutableStateOf<ImmutableList<CustomerJourneyCardModel>>(persistentListOf())
    private val hideBalance = mutableStateOf(false)
    private val hideIncome = mutableStateOf(false)
    private val expanded = mutableStateOf(true)

    @Composable
    override fun uiState(): HomeState {
        LaunchedEffect(Unit) {
            start()
        }

        return HomeState(
            theme = getTheme(),
            name = getName(),
            period = getPeriod(),
            baseData = getBaseData(),
            history = getHistory(),
            stats = getStats(),
            balance = getBalance(),
            buffer = getBuffer(),
            upcoming = getUpcoming(),
            overdue = getOverdue(),
            customerJourneyCards = getCustomerJourneyCards(),
            hideBalance = getHideBalance(),
            expanded = getExpanded(),
            hideIncome = getHideIncome()
        )
    }

    @Composable
    private fun getTheme(): Theme {
        return currentTheme.value
    }

    @Composable
    private fun getName(): String {
        return name.value
    }

    @Composable
    private fun getPeriod(): TimePeriod {
        return period.value
    }

    @Composable
    private fun getBaseData(): AppBaseData {
        return baseData.value
    }

    @Composable
    private fun getHistory(): ImmutableList<TransactionHistoryItem> {
        return history.value
    }

    @Composable
    private fun getStats(): IncomeExpensePair {
        return stats.value
    }

    @Composable
    private fun getBalance(): BigDecimal {
        return balance.value
    }

    @Composable
    private fun getBuffer(): BufferInfo {
        return buffer.value
    }

    @Composable
    private fun getUpcoming(): LegacyDueSection {
        return upcoming.value
    }

    @Composable
    private fun getOverdue(): LegacyDueSection {
        return overdue.value
    }

    @Composable
    private fun getCustomerJourneyCards(): ImmutableList<CustomerJourneyCardModel> {
        return customerJourneyCards.value
    }

    @Composable
    private fun getHideBalance(): Boolean {
        return hideBalance.value
    }

    @Composable
    private fun getExpanded(): Boolean {
        return expanded.value
    }

    @Composable
    private fun getHideIncome(): Boolean {
        return hideIncome.value
    }

    override fun onEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                HomeEvent.BalanceClick -> onBalanceClick()
                HomeEvent.HiddenBalanceClick -> onHiddenBalanceClick()
                HomeEvent.HiddenIncomeClick -> onHiddenIncomeClick()
                is HomeEvent.PayOrGetPlanned -> payOrGetPlanned(event.transaction)
                is HomeEvent.SkipPlanned -> skipPlanned(event.transaction)
                is HomeEvent.SkipAllPlanned -> skipAllPlanned(event.transactions)
                is HomeEvent.SetPeriod -> setPeriod(event.period)
                HomeEvent.SelectNextMonth -> onSelectNextMonth()
                HomeEvent.SelectPreviousMonth -> onSelectPreviousMonth()
                is HomeEvent.SetUpcomingExpanded -> setUpcomingExpanded(event.expanded)
                is HomeEvent.SetOverdueExpanded -> setOverdueExpanded(event.expanded)
                is HomeEvent.SetBuffer -> setBuffer(event.buffer)
                is HomeEvent.SetCurrency -> setCurrency(event.currency).fixUnit()
                HomeEvent.SwitchTheme -> switchTheme()
                is HomeEvent.DismissCustomerJourneyCard -> dismissCustomerJourneyCard(event.card)
                is HomeEvent.SetExpanded -> setExpanded(event.expanded)
            }
        }
    }

    private suspend fun start() {
        suspend {
            val startDay = startDayOfMonthAct(Unit)
            ivyContext.initSelectedPeriodInMemory(
                startDayOfMonth = startDay
            )
        } thenInvokeAfter ::reload
    }

    // -----------------------------------------------------------------------------------
    private suspend fun reload(
        timePeriod: TimePeriod = ivyContext.selectedPeriod
    ) = suspend {
        val settings = settingsAct(Unit)
        val hideBalance = shouldHideBalanceAct(Unit)
        val hideIncome = shouldHideIncomeAct(Unit)

        currentTheme.value = settings.theme
        name.value = settings.name
        period.value = timePeriod
        this.hideBalance.value = hideBalance
        this.hideIncome.value = hideIncome

        // This method is used to restore the theme when user imports locally backed up data
        ivyContext.switchTheme(theme = settings.theme)

        Pair(settings, period.value.toRange(ivyContext.startDayOfMonth).toCloseTimeRange())
    } then ::loadAppBaseData then ::loadIncomeExpenseBalance then
            ::loadBuffer then ::loadTrnHistory then
            ::loadDueTrns thenInvokeAfter ::loadCustomerJourney

    private suspend fun loadAppBaseData(
        input: Pair<Settings, ClosedTimeRange>
    ): Triple<Settings, ClosedTimeRange, List<Account>> =
        suspend {} then accountsAct then updateAccCacheAct then { accounts ->
            accounts
        } then { accounts ->
            val retrievedCategories = categoryRepository.findAll()
            val categories = updateCategoriesCacheAct(retrievedCategories)
            accounts to categories
        } thenInvokeAfter { (accounts, categories) ->
            val (settings, timeRange) = input

            baseData.value = AppBaseData(
                baseCurrency = settings.baseCurrency,
                categories = categories.toImmutableList(),
                accounts = accounts.toImmutableList()
            )

            Triple(settings, timeRange, accounts)
        }

    private suspend fun loadIncomeExpenseBalance(
        input: Triple<Settings, ClosedTimeRange, List<Account>>
    ): Triple<Settings, ClosedTimeRange, BigDecimal> {
        val (settings, timeRange, accounts) = input

        val incomeExpense = calcIncomeExpenseAct(
            CalcIncomeExpenseAct.Input(
                baseCurrency = settings.baseCurrency,
                accounts = accounts,
                range = timeRange
            )
        )

        val balanceAmount = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(baseCurrency = settings.baseCurrency)
        )

        balance.value = balanceAmount
        stats.value = incomeExpense

        return Triple(settings, timeRange, balanceAmount)
    }

    private suspend fun loadBuffer(
        input: Triple<Settings, ClosedTimeRange, BigDecimal>
    ): Pair<String, ClosedTimeRange> {
        val (settings, timeRange, balance) = input

        buffer.value = BufferInfo(
            amount = settings.bufferAmount,
            bufferDiff = calcBufferDiffAct(
                CalcBufferDiffAct.Input(
                    balance = balance,
                    buffer = settings.bufferAmount
                )
            )
        )

        return settings.baseCurrency to timeRange
    }

    private suspend fun loadTrnHistory(
        input: Pair<String, ClosedTimeRange>
    ): Pair<String, ClosedTimeRange> {
        val (baseCurrency, timeRange) = input

        history.value = historyWithDateDivsAct(
            HistoryWithDateDivsAct.Input(
                range = timeRange,
                baseCurrency = baseCurrency
            )
        )

        return baseCurrency to timeRange
    }

    private suspend fun loadDueTrns(
        input: Pair<String, ClosedTimeRange>
    ): Unit = suspend {
        UpcomingAct.Input(baseCurrency = input.first, range = input.second)
    } then upcomingAct then { result ->
        upcoming.value = LegacyDueSection(
            trns = with(transactionMapper) {
                result.upcomingTrns.map {
                    it.toEntity().toDomain()
                }.toImmutableList()
            },
            stats = result.upcoming,
            expanded = upcoming.value.expanded
        )
    } then {
        OverdueAct.Input(baseCurrency = input.first, toRange = input.second.to)
    } then overdueAct thenInvokeAfter { result ->
        overdue.value = LegacyDueSection(
            trns = with(transactionMapper) {
                result.overdueTrns.map {
                    it.toEntity().toDomain()
                }.toImmutableList()
            },
            stats = result.overdue,
            expanded = overdue.value.expanded
        )
    }

    private suspend fun loadCustomerJourney(unit: Unit) {
        customerJourneyCards.value = ioThread {
            customerJourneyLogic.loadCards().toImmutableList()
        }
    }
// -----------------------------------------------------------------

    private fun setUpcomingExpanded(expanded: Boolean) {
        upcoming.value = upcoming.value.copy(expanded = expanded)
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        overdue.value = overdue.value.copy(expanded = expanded)
    }

    private suspend fun onBalanceClick() {
        val hasTransactions = hasTrnsAct(Unit)
        if (hasTransactions) {
            // has transactions show him "Balance" screen
            nav.navigateTo(BalanceScreen)
        } else {
            // doesn't have transactions lead him to adjust balance
            ivyContext.selectMainTab(MainTab.ACCOUNTS)
            nav.navigateTo(MainScreen)
        }
    }

    private suspend fun onHiddenBalanceClick() {
        hideBalance.value = false

        // Showing Balance fow 5s
        delay(5000)

        hideBalance.value = true
    }

    private suspend fun onHiddenIncomeClick() {
        hideIncome.value = false

        // Showing Balance fow 5s
        delay(5000)

        hideIncome.value = true
    }

    private fun switchTheme() {
        viewModelScope.launch {
            settingsAct.getSettingsWithNextTheme().run {
                updateSettingsAct(this)
                ivyContext.switchTheme(this.theme)
                currentTheme.value = this.theme
            }
        }
    }

    private fun setBuffer(newBuffer: Double) {
        viewModelScope.launch {
            val currentSettings =
                settingsAct.getSettings().copy(bufferAmount = newBuffer.toBigDecimal())
            updateSettingsAct(currentSettings)
            buffer.value = buffer.value.copy(amount = currentSettings.bufferAmount)
        }
    }

    private suspend fun setCurrency(newCurrency: String) = settingsAct then {
        it.copy(
            baseCurrency = newCurrency
        )
    } then updateSettingsAct then {
        // update exchange rates from POV of the new base currency
        syncExchangeRatesUseCase.sync(newCurrency)
    } then {
        reload()
    }

    private suspend fun payOrGetPlanned(transaction: Transaction) {
        plannedPaymentsLogic.payOrGetLegacy(
            transaction = transaction,
            skipTransaction = false
        ) {
            reload()
        }
    }

    private suspend fun skipPlanned(transaction: Transaction) {
        plannedPaymentsLogic.payOrGetLegacy(
            transaction = transaction,
            skipTransaction = true
        ) {
            reload()
        }
    }

    private suspend fun skipAllPlanned(transactions: List<Transaction>) {
        plannedPaymentsLogic.payOrGetLegacy(
            transactions = transactions,
            skipTransaction = true
        ) {
            reload()
        }
    }

    private suspend fun dismissCustomerJourneyCard(card: CustomerJourneyCardModel) = suspend {
        customerJourneyLogic.dismissCard(card)
    } thenInvokeAfter {
        reload()
    }

    private suspend fun onSelectNextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        val period = month?.incrementMonthPeriod(ivyContext, 1L, year = year)
        if (period != null) {
            setPeriod(period)
        }
    }

    private suspend fun onSelectPreviousMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        val period = month?.incrementMonthPeriod(ivyContext, -1L, year = year)
        if (period != null) {
            setPeriod(period)
        }
    }

    private suspend fun setPeriod(period: TimePeriod) {
        reload(period)
    }

    private fun setExpanded(expanded: Boolean) {
        this.expanded.value = expanded
    }
}
