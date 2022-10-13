package com.ivy.accounts

import com.ivy.accounts.data.AccItemWithBalanceUi
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.data.AccountListItem
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.action.mapping.account.MapAccountFolderUiAct
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.design.l2_components.modal.IvyModal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountTabViewModel @Inject constructor(
    accountsFlow: AccountsFlow,
    private val accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapAccountFolderUiAct: MapAccountFolderUiAct,
    private val accBalanceFlow: AccBalanceFlow,
) : SimpleFlowViewModel<AccountTabState, AccountTabEvent>() {
    override val initialUi: AccountTabState = AccountTabState(
        items = emptyList(),
        createAccountModal = IvyModal()
    )

    override val uiFlow: Flow<AccountTabState> = solve().map { items ->
        AccountTabState(
            items = items,
            createAccountModal = initialUi.createAccountModal
        )
    }

    // TODO: Re-work this, it's just ugly!
    @OptIn(FlowPreview::class)
    private fun solve(): Flow<List<AccItemWithBalanceUi>> = accountFoldersFlow(Unit).map { items ->
        items.map { item ->
            when (item) {
                is AccountListItem.AccountHolder ->
                    item to listOf(accBalanceFlow(AccBalanceFlow.Input(item.account)))
                is AccountListItem.FolderHolder -> item to item.folder.accounts
                    .map { accBalanceFlow(AccBalanceFlow.Input(it)) }
            }
        }.map { (item, balanceFlows) ->
            if (balanceFlows.isEmpty())
                flowOf(item to listOf()) else combine(balanceFlows) { balances ->
                item to balances.toList()
            }
        }
    }.flatMapMerge { flows ->
        val combined = if (flows.isEmpty()) flowOf(emptyList()) else
            combine(flows) { itemBalancesList ->
                itemBalancesList.toList()
            }
        combined.map {
            it.map { (item, balances) ->
                when (item) {
                    is AccountListItem.AccountHolder ->
                        AccItemWithBalanceUi.AccountHolder(
                            account = mapAccountUiAct(item.account),
                            balance = format(balances.first(), shortenFiat = false),
                        )
                    is AccountListItem.FolderHolder -> AccItemWithBalanceUi.FolderHolder(
                        folder = mapAccountFolderUiAct(item.folder),
                        accItems = item.folder.accounts.mapIndexed { index, acc ->
                            AccItemWithBalanceUi.AccountHolder(
                                account = mapAccountUiAct(acc),
                                balance = format(balances[index], shortenFiat = false),
                            )
                        },
                        balance = ValueUi("0.00", "USD") // TODO: Implement that
                    )
                }
            }
        }
    }

    // region Event Handling
    override suspend fun handleEvent(event: AccountTabEvent) = when (event) {
        is AccountTabEvent.BottomBarAction -> handleBottomBarAction(event)
    }

    private fun handleBottomBarAction(event: AccountTabEvent.BottomBarAction) {
        // TODO: Handle properly
        uiState.value.createAccountModal.show()
    }
    // endregion
}