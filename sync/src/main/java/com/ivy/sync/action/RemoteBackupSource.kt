package com.ivy.sync.action

import arrow.core.Either
import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.domain.api.data.ActionError

interface RemoteBackupSource {
    suspend fun fetchBackup(): Either<ActionError, IvyWalletData>

    suspend fun uploadBackup(updated: IvyWalletData): Either<ActionError, Unit>
}