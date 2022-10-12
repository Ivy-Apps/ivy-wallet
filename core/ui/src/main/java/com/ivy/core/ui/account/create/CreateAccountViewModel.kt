package com.ivy.core.ui.account.create

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class CreateAccountViewModel @Inject constructor(
    private val writeAccountsAct: WriteAccountsAct,
    private val itemIconAct: ItemIconAct,
) : SimpleFlowViewModel<CreateAccountModalState, CreateAccountModalEvent>() {
    override val initialUi = CreateAccountModalState(
        currency = "o",
        icon = ItemIcon.Sized(
            iconS = R.drawable.ic_custom_account_s,
            iconM = R.drawable.ic_custom_account_m,
            iconL = R.drawable.ic_custom_account_l,
            iconId = null
        )
    )

    private val currency = MutableStateFlow("")
    private val iconId = MutableStateFlow<ItemIconId?>(null)

    override val uiFlow: Flow<CreateAccountModalState> = combine(
        currency, iconId
    ) { currency, iconId ->
        CreateAccountModalState(
            currency = currency,
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

    private fun createAccount(event: CreateAccountModalEvent.CreateAccount) {
        // TODO:
    }

    private fun handleIconPick(event: CreateAccountModalEvent.IconPick) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: CreateAccountModalEvent.NameChange) {
        // TODO:
    }

    private fun handleCurrencyChange(event: CreateAccountModalEvent.CurrencyChange) {
        currency.value = event.newCurrency
    }
    // endregion
}