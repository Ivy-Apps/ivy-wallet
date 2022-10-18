package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.WriteAccountsAct
import javax.inject.Inject

class WriteFolderAccountsAct @Inject constructor(
    private val writeAccountsAct: WriteAccountsAct,
) : Action<WriteFolderAccountsAct.Input, Unit>() {
    data class Input(
        val folderId: String,
        val accountIds: List<String>
    )

    override suspend fun Input.willDo() {
        TODO("Not yet implemented")
    }
}