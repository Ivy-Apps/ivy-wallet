package com.ivy.accounts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import com.ivy.core.datamodel.Account
import com.ivy.core.db.write.AccountWriter
import com.ivy.core.event.AccountUpdatedEvent
import com.ivy.core.event.EventBus
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.ioThread
import com.ivy.resources.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.viewmodel.account.AccountDataAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
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
) : ComposeViewModel<AccountState, AccountsEvent>() {
    val accountState = mutableStateOf(AccountState())

    @Composable
    override fun uiState(): AccountState {
        return AccountState(
            baseCurrency =,
            accountsData = persistentListOf(),
            totalBalanceWithExcluded = 0.0,
            totalBalanceWithExcludedText =,
            reorderVisible = false
        )
    }

    fun onStart() {
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

        val totalBalanceWithExcluded = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(
                baseCurrency = baseCurrencyCode,
                withExcluded = true
            )
        ).toDouble()

        updateState {
            it.copy(
                baseCurrency = baseCurrencyCode,
                accountsData = accountsDataList,
                totalBalanceWithExcluded = totalBalanceWithExcluded,
                totalBalanceWithExcludedText = com.ivy.legacy.utils.UiText.StringResource(
                    R.string.total,
                    baseCurrencyCode,
                    totalBalanceWithExcluded.format(
                        baseCurrencyCode
                    )
                )
            )
        }
    }

    init {
        viewModelScope.launch {
            eventBus.subscribe(AccountUpdatedEvent) {
                onStart()
            }
        }
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

    private suspend fun reorder(newOrder: List<com.ivy.legacy.data.model.AccountData>) {
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

    private suspend fun reorderModalVisible(reorderVisible: Boolean) {
        updateState {
            it.copy(reorderVisible = reorderVisible)
        }
    }
}