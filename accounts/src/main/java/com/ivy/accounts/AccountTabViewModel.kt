package com.ivy.accounts

import com.ivy.accounts.data.AccListItemUi
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.exchange.ExchangeFlow
import com.ivy.core.domain.action.exchange.SumValuesInCurrencyFlow
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.domain.pure.util.combineList
import com.ivy.core.ui.action.mapping.account.MapAccountFolderUiAct
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.data.Value
import com.ivy.design.l2_components.modal.IvyModal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountTabViewModel @Inject constructor(
    private val accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapAccountFolderUiAct: MapAccountFolderUiAct,
    private val accBalanceFlow: AccBalanceFlow,
    private val sumValuesInCurrencyFlow: SumValuesInCurrencyFlow,
    private val exchangeFlow: ExchangeFlow,
) : SimpleFlowViewModel<AccountTabState, AccountTabEvent>() {
    override val initialUi: AccountTabState = AccountTabState(
        items = emptyList(),
        createModal = IvyModal()
    )

    override val uiFlow: Flow<AccountTabState> = accListItemsUiFlow().map { items ->
        AccountTabState(
            items = items,
            createModal = initialUi.createModal
        )
    }

    // TODO: Re-work this, it's just ugly!
    @OptIn(FlowPreview::class)
    private fun accListItemsUiFlow(): Flow<List<AccListItemUi>> =
        accountFoldersFlow(Unit).map { items ->
            items.map { item ->
                when (item) {
                    is AccountListItem.AccountHolder ->
                        item to listOf(accBalanceFlow(AccBalanceFlow.Input(item.account)))
                    is AccountListItem.FolderHolder -> item to item.folder
                        .accounts.map { accBalanceFlow(AccBalanceFlow.Input(it)) }
                }
            }.map { (item, balanceFlows) ->
                // Handle empty folders with no accounts inside
                if (balanceFlows.isEmpty())
                    flowOf(item to listOf()) else combine(balanceFlows) { balances ->
                    item to balances.toList()
                }
            }
        }.flatMapMerge(transform = ::combineList)
            .map(::toAccListItemsUi)
            .flatMapMerge(transform = ::combineList)

    private suspend fun toAccListItemsUi(
        itemBalances: List<Pair<AccountListItem, List<Value>>>
    ): List<Flow<AccListItemUi>> = itemBalances.map { (item, balances) ->
        when (item) {
            is AccountListItem.AccountHolder -> {
                val accBalance = balances.first()
                exchangeFlow(ExchangeFlow.Input(accBalance)).map { balanceBaseCurrency ->
                    AccListItemUi.AccountHolder(
                        account = mapAccountUiAct(item.account),
                        balance = format(accBalance, shortenFiat = false),
                        balanceBaseCurrency = balanceBaseCurrency(
                            baseCurrency = balanceBaseCurrency,
                            currency = accBalance,
                        )
                    )
                }
            }
            is AccountListItem.FolderHolder -> combine(
                sumValuesInCurrencyFlow(SumValuesInCurrencyFlow.Input(balances)),
                combineList(balances.map { exchangeFlow(ExchangeFlow.Input(it)) })
            ) { folderBalance, balancesBaseCurrency ->
                AccListItemUi.FolderHolder(
                    folder = mapAccountFolderUiAct(item.folder),
                    accItems = item.folder.accounts.mapIndexed { index, acc ->
                        AccListItemUi.AccountHolder(
                            account = mapAccountUiAct(acc),
                            balance = format(balances[index], shortenFiat = false),
                            balanceBaseCurrency = balanceBaseCurrency(
                                baseCurrency = balancesBaseCurrency[index],
                                currency = balances[index]
                            )
                        )
                    },
                    balance = format(folderBalance, shortenFiat = true)
                )
            }
        }
    }

    private fun balanceBaseCurrency(
        baseCurrency: Value,
        currency: Value
    ): ValueUi? = baseCurrency.takeIf {
        it.currency != currency.currency && it.amount > 0.0
    }?.let { format(it, shortenFiat = true) }

    // region Event Handling
    override suspend fun handleEvent(event: AccountTabEvent) = when (event) {
        is AccountTabEvent.BottomBarAction -> handleBottomBarAction(event)
    }

    private fun handleBottomBarAction(event: AccountTabEvent.BottomBarAction) {
        // TODO: Handle properly
        uiState.value.createModal.show()
    }
// endregion
}