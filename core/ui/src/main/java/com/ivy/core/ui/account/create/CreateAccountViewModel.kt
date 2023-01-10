package com.ivy.core.ui.account.create

import androidx.compose.ui.graphics.toArgb
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.NewAccountTabItemOrderNumAct
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.*
import javax.inject.Inject

@HiltViewModel
internal class CreateAccountViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeAccountsAct: WriteAccountsAct,
    private val newAccountTabItemOrderNumAct: NewAccountTabItemOrderNumAct,
    baseCurrencyFlow: BaseCurrencyFlow,
    private val timeProvider: TimeProvider,
) : SimpleFlowViewModel<CreateAccountState, CreateAccountEvent>() {
    override val initialUi = CreateAccountState(
        currency = "",
        icon = ItemIcon.Sized(
            iconS = R.drawable.ic_custom_account_s,
            iconM = R.drawable.ic_custom_account_m,
            iconL = R.drawable.ic_custom_account_l,
            iconId = null
        )
    )

    private var name = ""
    private val currency = MutableStateFlow<CurrencyCode?>(null)
    private val iconId = MutableStateFlow<ItemIconId?>(null)

    override val uiFlow: Flow<CreateAccountState> = combine(
        baseCurrencyFlow(), currency, iconId
    ) { baseCurrency, currency, iconId ->
        CreateAccountState(
            currency = currency ?: baseCurrency,
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Account))
        )
    }

    // region Event Handling
    override suspend fun handleEvent(event: CreateAccountEvent) = when (event) {
        is CreateAccountEvent.CreateAccount -> createAccount(event)
        is CreateAccountEvent.IconChange -> handleIconPick(event)
        is CreateAccountEvent.NameChange -> handleNameChange(event)
        is CreateAccountEvent.CurrencyChange -> handleCurrencyChange(event)
    }

    private suspend fun createAccount(event: CreateAccountEvent.CreateAccount) {
        val newAccount = Account(
            id = UUID.randomUUID(),
            name = name,
            currency = uiState.value.currency,
            color = event.color.toArgb(),
            icon = iconId.value,
            excluded = event.excluded,
            folderId = event.folder?.id?.toUUID(),
            orderNum = newAccountTabItemOrderNumAct(Unit),
            state = AccountState.Default,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            ),
        )
        writeAccountsAct(Modify.save(newAccount))
    }

    private fun handleIconPick(event: CreateAccountEvent.IconChange) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: CreateAccountEvent.NameChange) {
        name = event.name
    }

    private fun handleCurrencyChange(event: CreateAccountEvent.CurrencyChange) {
        currency.value = event.newCurrency
    }
    // endregion
}