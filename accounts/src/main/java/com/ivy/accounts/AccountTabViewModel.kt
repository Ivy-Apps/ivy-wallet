package com.ivy.accounts

import com.ivy.accounts.data.AccountListItemUi
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.exchange.ExchangeFlow
import com.ivy.core.domain.action.exchange.SumValuesInCurrencyFlow
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.domain.pure.util.combineList
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import com.ivy.data.Value
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.main.base.MainBottomBarVisibility
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.navigation.destinations.main.Main
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountTabViewModel @Inject constructor(
    private val accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapFolderUiAct: MapFolderUiAct,
    private val accBalanceFlow: AccBalanceFlow,
    private val sumValuesInCurrencyFlow: SumValuesInCurrencyFlow,
    private val exchangeFlow: ExchangeFlow,
    private val totalBalanceFlow: TotalBalanceFlow,
    private val navigator: Navigator,
    private val mainBottomBarVisibility: MainBottomBarVisibility,
) : SimpleFlowViewModel<AccountTabState, AccountTabEvent>() {
    override val initialUi: AccountTabState = AccountTabState(
        totalBalance = ValueUi("", ""),
        availableBalance = ValueUi("", ""),
        excludedBalance = ValueUi("", ""),
        items = emptyList(),
        noAccounts = false,
        createModal = IvyModal()
    )

    override val uiFlow: Flow<AccountTabState> = combine(
        accListItemsUiFlow(), totalBalanceFlow(), availableBalanceFlow()
    ) { items, totalBalance, availableBalance ->
        val excludedBalance = Value(
            amount = totalBalance.amount - availableBalance.amount,
            currency = totalBalance.currency
        )
        AccountTabState(
            totalBalance = format(totalBalance, shortenFiat = true),
            availableBalance = format(availableBalance, shortenFiat = true),
            excludedBalance = format(excludedBalance, shortenFiat = true),
            noAccounts = items.none {
                // no items (accounts) that match the predicate
                when (it) {
                    is AccountListItemUi.AccountWithBalance -> true
                    is AccountListItemUi.Archived -> it.accountsCount > 0
                    is AccountListItemUi.FolderWithAccounts -> it.accountsCount > 0
                }
            },
            items = items,
            createModal = initialUi.createModal
        )
    }

    private fun totalBalanceFlow(): Flow<Value> = totalBalanceFlow(
        TotalBalanceFlow.Input(withExcludedAccs = true)
    )

    private fun availableBalanceFlow(): Flow<Value> = totalBalanceFlow(
        TotalBalanceFlow.Input(withExcludedAccs = false)
    )

    // TODO: Re-work this, it's just ugly!
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun accListItemsUiFlow(): Flow<List<AccountListItemUi>> =
        accountFoldersFlow(Unit).map { items ->
            items
                .filter {
                    when (it) {
                        is AccountListItem.AccountHolder -> true
                        // allow empty folders
                        is AccountListItem.FolderWithAccounts -> true
                        is AccountListItem.Archived -> it.accounts.isNotEmpty()
                    }
                }
                .map { item ->
                    when (item) {
                        is AccountListItem.AccountHolder ->
                            item to listOf(accBalanceFlow(AccBalanceFlow.Input(item.account)))
                        is AccountListItem.FolderWithAccounts -> item to item.accounts
                            .map { accBalanceFlow(AccBalanceFlow.Input(it)) }
                        is AccountListItem.Archived -> item to item.accounts
                            .map { accBalanceFlow(AccBalanceFlow.Input(it)) }
                    }
                }.map { (item, balanceFlows) ->
                    // Handle empty folders with no accounts inside
                    if (balanceFlows.isEmpty())
                        flowOf(item to listOf()) else combine(balanceFlows) { balances ->
                        item to balances.toList()
                    }
                }
        }.flatMapLatest(transform = ::combineList)
            .map(::toAccListItemsUi)
            .flatMapLatest(transform = ::combineList)

    private suspend fun toAccListItemsUi(
        itemBalances: List<Pair<AccountListItem, List<Value>>>
    ): List<Flow<AccountListItemUi>> = itemBalances.map { (item, balances) ->
        when (item) {
            is AccountListItem.AccountHolder -> {
                val accBalance = balances.first()
                exchangeFlow(ExchangeFlow.Input(accBalance)).map { balanceBaseCurrency ->
                    AccountListItemUi.AccountWithBalance(
                        account = mapAccountUiAct(item.account),
                        balance = format(accBalance, shortenFiat = false),
                        balanceBaseCurrency = balanceBaseCurrency(
                            baseCurrency = balanceBaseCurrency,
                            currency = accBalance,
                        )
                    )
                }
            }
            is AccountListItem.FolderWithAccounts -> combine(
                sumValuesInCurrencyFlow(SumValuesInCurrencyFlow.Input(balances)),
                combineList(balances.map { exchangeFlow(ExchangeFlow.Input(it)) })
            ) { folderBalance, balancesBaseCurrency ->
                AccountListItemUi.FolderWithAccounts(
                    folder = mapFolderUiAct(item.folder),
                    accItems = item.accounts.mapIndexed { index, acc ->
                        AccountListItemUi.AccountWithBalance(
                            account = mapAccountUiAct(acc),
                            balance = format(balances[index], shortenFiat = false),
                            balanceBaseCurrency = balanceBaseCurrency(
                                baseCurrency = balancesBaseCurrency[index],
                                currency = balances[index]
                            )
                        )
                    },
                    accountsCount = item.accounts.size,
                    balance = format(folderBalance, shortenFiat = true)
                )
            }
            is AccountListItem.Archived -> combineList(
                balances.map { exchangeFlow(ExchangeFlow.Input(it)) }
            ).map { balancesBaseCurrency ->
                AccountListItemUi.Archived(
                    accHolders = item.accounts.mapIndexed { index, acc ->
                        AccountListItemUi.AccountWithBalance(
                            account = mapAccountUiAct(acc),
                            balance = format(balances[index], shortenFiat = false),
                            balanceBaseCurrency = balanceBaseCurrency(
                                baseCurrency = balancesBaseCurrency[index],
                                currency = balances[index]
                            )
                        )
                    },
                    accountsCount = item.accounts.size,
                )
            }
        }
    }

    private fun balanceBaseCurrency(
        baseCurrency: Value,
        currency: Value
    ): ValueUi? = baseCurrency.takeIf {
        it.currency != currency.currency && it.amount != 0.0
    }?.let { format(it, shortenFiat = true) }


    // region Event Handling
    override suspend fun handleEvent(event: AccountTabEvent) = when (event) {
        is AccountTabEvent.BottomBarAction -> handleBottomBarAction(event)
        AccountTabEvent.NavigateToHome -> handleNavigateToHome()
        AccountTabEvent.HideBottomBar -> handleHideBottomBar()
        AccountTabEvent.ShowBottomBar -> handleShowBottomBar()
    }

    private fun handleBottomBarAction(event: AccountTabEvent.BottomBarAction) {
        // TODO: Implement special handling for the gesture (swipe up, left, etc)
        uiState.value.createModal.show()
    }

    private fun handleNavigateToHome() {
        navigator.navigate(Destination.main.destination(Main.Tab.Home)) {
            popUpTo(Destination.main.route) {
                inclusive = true
            }
        }
    }

    private fun handleShowBottomBar() {
        mainBottomBarVisibility.visible.value = true
    }

    private fun handleHideBottomBar() {
        mainBottomBarVisibility.visible.value = false
    }
    // endregion
}