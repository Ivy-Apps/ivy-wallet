package com.ivy.core.domain.action.account.folder

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.account.folder.WriteAccountFolderContentAct.Input
import com.ivy.core.domain.action.data.Modify
import com.ivy.data.Sync
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class WriteAccountFolderContentAct @Inject constructor(
    private val writeAccountsAct: WriteAccountsAct,
    private val accountsFlow: AccountsFlow,
    private val timeProvider: TimeProvider,
) : Action<Input, Unit>() {
    data class Input(
        val folderId: String,
        val accountIds: List<String>
    )

    override suspend fun action(input: Input) {
        val folderUUID = input.folderId.toUUID()
        val accounts = accountsFlow().take(1).first()
        val inFolderOld = accounts.filter { it.folderId == folderUUID }

        // remove accounts no longer in folder
        val removeFromFolder = inFolderOld.filter { !input.accountIds.contains(it.id.toString()) }
            .map {
                it.copy(
                    folderId = null,
                    sync = Sync(
                        state = SyncState.Syncing,
                        lastUpdated = timeProvider.timeNow(),
                    )
                )
            }
        writeAccountsAct(Modify.saveMany(removeFromFolder))

        // add new accounts to that folder
        val addToFolder = accounts.filter { input.accountIds.contains(it.id.toString()) }
            .filter { !inFolderOld.contains(it) }
            .map {
                it.copy(
                    folderId = folderUUID,
                    sync = Sync(
                        state = SyncState.Syncing,
                        lastUpdated = timeProvider.timeNow(),
                    )
                )
            }
        writeAccountsAct(Modify.saveMany(addToFolder))
    }
}