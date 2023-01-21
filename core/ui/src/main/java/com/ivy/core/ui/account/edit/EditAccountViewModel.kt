package com.ivy.core.ui.account.edit

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.*
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
internal class EditAccountViewModel @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val itemIconAct: ItemIconAct,
    private val writeAccountsAct: WriteAccountsAct,
    private val accountByIdAct: AccountByIdAct,
    private val accountFoldersFlow: AccountFoldersFlow,
    private val mapFolderUiAct: MapFolderUiAct,
    private val accBalanceFlow: AccBalanceFlow,
    private val timeProvider: TimeProvider,
) : SimpleFlowViewModel<EditAccountState, EditAccountEvent>() {
    override val initialUi = EditAccountState(
        accountId = "",
        currency = "",
        icon = ItemIcon.Sized(
            iconS = R.drawable.ic_custom_account_s,
            iconM = R.drawable.ic_custom_account_m,
            iconL = R.drawable.ic_custom_account_l,
            iconId = null
        ),
        color = Purple,
        initialName = "",
        folder = null,
        excluded = false,
        archived = false,
        balance = Value(0.0, ""),
        balanceUi = ValueUi("0.00", ""),
    )

    private val account = MutableStateFlow<Account?>(null)
    private var name = ""
    private val initialName = MutableStateFlow(initialUi.initialName)
    private val currency = MutableStateFlow(initialUi.currency)
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val color = MutableStateFlow(initialUi.color)
    private val excluded = MutableStateFlow(initialUi.excluded)
    private val folderId = MutableStateFlow<String?>(null)
    private val archived = MutableStateFlow(initialUi.archived)

    override val uiFlow: Flow<EditAccountState> = combine(
        account, headerFlow(), secondaryFlow(), folderFlow(), accountBalanceFlow()
    ) { account, header, secondary, folder, balance ->
        EditAccountState(
            accountId = account?.id?.toString() ?: "",
            currency = secondary.currency,
            icon = itemIconAct(ItemIconAct.Input(header.iconId, DefaultTo.Account)),
            initialName = header.initialName,
            color = header.color,
            excluded = secondary.excluded,
            folder = folder,
            archived = secondary.archived,
            balance = balance ?: initialUi.balance,
            balanceUi = balance?.let { format(it, shortenFiat = false) } ?: initialUi.balanceUi
        )
    }

    private fun headerFlow(): Flow<Header> = combine(
        iconId, initialName, color,
    ) { iconId, initialName, color ->
        Header(iconId = iconId, initialName = initialName, color = color)
    }

    private fun secondaryFlow(): Flow<Secondary> = combine(
        currency, excluded, archived
    ) { currency, excluded, archived ->
        Secondary(currency, excluded, archived)
    }

    private fun folderFlow(): Flow<FolderUi?> = combine(
        accountFoldersFlow(Unit), folderId
    ) { folders, folderId ->
        folders.filterIsInstance<AccountListItem.FolderWithAccounts>()
            .firstOrNull { it.accountFolder.id == folderId }
            ?.let { mapFolderUiAct(it.accountFolder) }
    }

    @OptIn(FlowPreview::class)
    private fun accountBalanceFlow(): Flow<Value?> = account.flatMapLatest { account ->
        if (account != null) {
            accBalanceFlow(AccBalanceFlow.Input(account))
        } else flowOf(null)
    }

    // region Event Handling
    override suspend fun handleEvent(event: EditAccountEvent) = when (event) {
        is EditAccountEvent.Initial -> handleInitial(event)
        EditAccountEvent.EditAccount -> editAccount()
        is EditAccountEvent.IconChange -> handleIconPick(event)
        is EditAccountEvent.NameChange -> handleNameChange(event)
        is EditAccountEvent.CurrencyChange -> handleCurrencyChange(event)
        is EditAccountEvent.ColorChange -> handleColorChange(event)
        is EditAccountEvent.ExcludedChange -> handleExcludedChange(event)
        is EditAccountEvent.FolderChange -> handleFolderChange(event)
        EditAccountEvent.Archive -> handleArchive()
        EditAccountEvent.Unarchive -> handleUnarchive()
        EditAccountEvent.Delete -> handleDelete()
    }

    private suspend fun handleInitial(event: EditAccountEvent.Initial) {
        // we need a snapshot of the account at this given point in time
        // => flow isn't good for that use-case
        accountByIdAct(event.accountId)?.let {
            account.value = it
            name = it.name
            initialName.value = it.name
            currency.value = it.currency
            iconId.value = it.icon
            color.value = it.color.toComposeColor()
            excluded.value = it.excluded
            folderId.value = it.folderId?.toString()
            archived.value = it.state == AccountState.Archived
        }
    }

    private suspend fun editAccount() {
        val updatedAccount = account.value?.copy(
            name = name,
            currency = currency.value,
            color = color.value.toArgb(),
            folderId = folderId.value?.toUUID(),
            excluded = excluded.value,
            icon = iconId.value,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )
        if (updatedAccount != null) {
            writeAccountsAct(Modify.save(updatedAccount))
        }
    }

    private fun handleIconPick(event: EditAccountEvent.IconChange) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: EditAccountEvent.NameChange) {
        name = event.name
    }

    private fun handleCurrencyChange(event: EditAccountEvent.CurrencyChange) {
        currency.value = event.newCurrency
    }

    private fun handleColorChange(event: EditAccountEvent.ColorChange) {
        color.value = event.color
    }

    private fun handleFolderChange(event: EditAccountEvent.FolderChange) {
        folderId.value = event.folder?.id
    }

    private fun handleExcludedChange(event: EditAccountEvent.ExcludedChange) {
        excluded.value = event.excluded
    }

    private suspend fun handleArchive() {
        archived.value = true
        updateArchived(state = AccountState.Archived)
        showToast("Account archived")
    }

    private suspend fun handleUnarchive() {
        archived.value = false
        updateArchived(state = AccountState.Default)
        showToast("Account unarchived")
    }

    private fun showToast(text: String) {
        Toast.makeText(appContext, text, Toast.LENGTH_LONG).show()
    }

    private suspend fun updateArchived(state: AccountState) {
        val updatedAccount = account.value?.copy(
            state = state,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )
        if (updatedAccount != null) {
            writeAccountsAct(Modify.save(updatedAccount))
        }
    }

    private suspend fun handleDelete() {
        account.value?.let {
            writeAccountsAct(Modify.delete(it.id.toString()))
        }
    }
    // endregion

    private data class Header(
        val iconId: ItemIconId?,
        val initialName: String,
        val color: Color,
    )

    private data class Secondary(
        val currency: CurrencyCode,
        val excluded: Boolean,
        val archived: Boolean,
    )
}