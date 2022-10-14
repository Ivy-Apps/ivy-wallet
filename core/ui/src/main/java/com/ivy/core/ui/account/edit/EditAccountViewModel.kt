package com.ivy.core.ui.account.edit

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivy.common.toUUID
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.account.MapAccountFolderUiAct
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class EditAccountViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeAccountsAct: WriteAccountsAct,
    private val accountByIdAct: AccountByIdAct,
    private val accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountFolderUiAct: MapAccountFolderUiAct
) : SimpleFlowViewModel<EditAccountState, EditAccountEvent>() {
    override val initialUi = EditAccountState(
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
    )

    private var account: Account? = null
    private var name = ""
    private val initialName = MutableStateFlow(initialUi.initialName)
    private val currency = MutableStateFlow(initialUi.currency)
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val color = MutableStateFlow(initialUi.color)
    private val excluded = MutableStateFlow(initialUi.excluded)
    private val folderId = MutableStateFlow<String?>(null)
    private val archived = MutableStateFlow(initialUi.archived)

    override val uiFlow: Flow<EditAccountState> = combine(
        headerFlow(), currency, excluded, folderFlow(), archived,
    ) { header, currency, excluded, folder, archived ->
        EditAccountState(
            currency = currency,
            icon = itemIconAct(ItemIconAct.Input(header.iconId, DefaultTo.Account)),
            initialName = header.initialName,
            color = header.color,
            excluded = excluded,
            folder = folder,
            archived = archived
        )
    }

    private fun headerFlow(): Flow<Header> = combine(
        iconId, initialName, color,
    ) { iconId, initialName, color ->
        Header(iconId = iconId, initialName = initialName, color = color)
    }

    private fun folderFlow(): Flow<AccountFolderUi?> = combine(
        accountFoldersFlow(Unit), folderId
    ) { folders, folderId ->
        folders.filterIsInstance<AccountListItem.FolderHolder>()
            .firstOrNull { it.folder.id == folderId }
            ?.let { mapAccountFolderUiAct(it.folder) }
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
        EditAccountEvent.Delete -> handleDelete()
    }

    private suspend fun handleInitial(event: EditAccountEvent.Initial) {
        // we need a snapshot of the account at this given point in time
        // => flow isn't good for that use-case
        accountByIdAct(event.accountId)?.let {
            account = it
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
        val updatedAccount = account?.copy(
            name = name,
            currency = currency.value,
            color = color.value.toArgb(),
            folderId = folderId.value?.toUUID(),
            excluded = excluded.value,
            icon = iconId.value
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
        val updatedAccount = account?.copy(state = AccountState.Archived)
        if (updatedAccount != null) {
            writeAccountsAct(Modify.save(updatedAccount))
        }
    }

    private suspend fun handleDelete() {
        account?.let {
            writeAccountsAct(Modify.delete(it.id.toString()))
        }
    }
    // endregion

    private data class Header(
        val iconId: ItemIconId?,
        val initialName: String,
        val color: Color,
    )
}