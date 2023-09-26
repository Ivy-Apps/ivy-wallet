package com.ivy.accounts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import com.ivy.core.datamodel.Account
import com.ivy.core.db.write.AccountWriter
import com.ivy.core.event.AccountUpdatedEvent
import com.ivy.core.event.EventBus
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.AccountData
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.UiText
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.ioThread
import com.ivy.resources.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.viewmodel.account.AccountDataAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountCreator: AccountCreator,
    private val ivyContext: com.ivy.legacy.IvyWalletCtx,
    private val sharedPrefs: SharedPrefs,
    private val accountsAct: AccountsAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountDataAct: AccountDataAct,
    private val eventBus: EventBus,
    private val accountWriter: AccountWriter,
) : ComposeViewModel<AccountsState, AccountsEvent>() {
    private val baseCurrency = mutableStateOf("")
    private val accountsData = mutableStateOf(listOf<AccountData>())
    private val totalBalanceWithExcluded = mutableStateOf("")
    private val totalBalanceWithExcludedText = mutableStateOf<UiText>(UiText.DynamicString(""))
    private val reorderVisible = mutableStateOf(false)

    @Composable
    override fun uiState(): AccountsState {
        LaunchedEffect(Unit) {
            onStart()
        }

        return AccountsState(
            baseCurrency = getBaseCurrency(),
            accountsData = getAccountsData(),
            totalBalanceWithExcluded = getTotalBalanceWithExcluded(),
            totalBalanceWithExcludedText = getTotalBalanceWithExcludedText(),
            reorderVisible = getReorderVisible()
        )
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getAccountsData(): ImmutableList<AccountData> {
        return accountsData.value.toImmutableList()
    }

    @Composable
    private fun getTotalBalanceWithExcluded(): String {
        return totalBalanceWithExcluded.value
    }

    @Composable
    private fun getTotalBalanceWithExcludedText(): UiText {
        return totalBalanceWithExcludedText.value
    }

    @Composable
    private fun getReorderVisible(): Boolean {
        return reorderVisible.value
    }

    override fun onEvent(event: AccountsEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is AccountsEvent.OnReorder -> reorder(event.reorderedList)
                is AccountsEvent.OnEditAccount -> editAccount(event.editedAccount, event.newBalance)
                is AccountsEvent.OnReorderModalVisible -> reorderModalVisible(event.reorderVisible)
            }
        }
    }

    private suspend fun reorder(newOrder: List<AccountData>) {
        ioThread {
            newOrder.mapIndexed { index, accountData ->
                accountWriter.save(
                    accountData.account.toEntity().copy(
                        orderNum = index.toDouble(),
                        isSynced = false
                    )
                )
            }
        }

        startInternally()
    }

    private suspend fun editAccount(account: Account, newBalance: Double) {
        accountCreator.editAccount(account, newBalance) {
            startInternally()
        }
    }

    init {
        viewModelScope.launch {
            eventBus.subscribe(AccountUpdatedEvent) {
                onStart()
            }
        }
    }

    private fun onStart() {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally()
        }
    }

    private suspend fun startInternally() {
        val period = com.ivy.legacy.data.model.TimePeriod.currentMonth(
            startDayOfMonth = ivyContext.startDayOfMonth
        ) // this must be monthly
        val range = period.toRange(ivyContext.startDayOfMonth)

        val baseCurrencyCode = baseCurrencyAct(Unit)
        val accs = accountsAct(Unit)

        val includeTransfersInCalc =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

        val accountsDataList = accountDataAct(
            AccountDataAct.Input(
                accounts = accs,
                range = range.toCloseTimeRange(),
                baseCurrency = baseCurrencyCode,
                includeTransfersInCalc = includeTransfersInCalc
            )
        )

        val totalBalanceIncludingExcluded = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(
                baseCurrency = baseCurrencyCode,
                withExcluded = true
            )
        ).toDouble()

        baseCurrency.value = baseCurrencyCode
        accountsData.value = accountsDataList
        totalBalanceWithExcluded.value = totalBalanceIncludingExcluded.toString()
        totalBalanceWithExcludedText.value = UiText.StringResource(
            R.string.total,
            baseCurrencyCode,
            totalBalanceIncludingExcluded.format(
                baseCurrencyCode
            )
        )
    }

    private fun reorderModalVisible(visible: Boolean) {
        reorderVisible.value = visible
    }
}