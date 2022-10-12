package com.ivy.core.ui.account.create

import androidx.compose.ui.graphics.toArgb
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.NewAccountOrderNumAct
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
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
    private val newAccountOrderNumAct: NewAccountOrderNumAct,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : SimpleFlowViewModel<CreateAccountModalState, CreateAccountModalEvent>() {
    override val initialUi = CreateAccountModalState(
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

    override val uiFlow: Flow<CreateAccountModalState> = combine(
        baseCurrencyFlow(), currency, iconId
    ) { baseCurrency, currency, iconId ->
        CreateAccountModalState(
            currency = currency ?: baseCurrency,
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Account))
        )
    }

    // region Event Handling
    override suspend fun handleEvent(event: CreateAccountModalEvent) = when (event) {
        is CreateAccountModalEvent.CreateAccount -> createAccount(event)
        is CreateAccountModalEvent.IconPick -> handleIconPick(event)
        is CreateAccountModalEvent.NameChange -> handleNameChange(event)
        is CreateAccountModalEvent.CurrencyChange -> handleCurrencyChange(event)
    }

    private suspend fun createAccount(event: CreateAccountModalEvent.CreateAccount) {
        val newAccount = Account(
            id = UUID.randomUUID(),
            name = name,
            currency = uiState.value.currency,
            color = event.color.toArgb(),
            icon = iconId.value,
            excluded = event.excluded,
            folderId = null, // TODO: Implement account folders
            orderNum = newAccountOrderNumAct(Unit),
            state = AccountState.Default,
            sync = SyncState.Syncing
        )
        writeAccountsAct(Modify.save(newAccount))
    }

    private fun handleIconPick(event: CreateAccountModalEvent.IconPick) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: CreateAccountModalEvent.NameChange) {
        name = event.name
    }

    private fun handleCurrencyChange(event: CreateAccountModalEvent.CurrencyChange) {
        currency.value = event.newCurrency
    }
    // endregion
}