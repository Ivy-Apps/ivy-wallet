package com.ivy.sync.action

import arrow.core.Either
import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.domain.api.data.ActionError
import javax.inject.Inject

class RemoteBackupSourceImpl @Inject constructor(

) : RemoteBackupSource {
    override suspend fun fetchBackup(): Either<ActionError, IvyWalletData> {
        TODO("Fetch Backup from Google Drive")
    }

    override suspend fun uploadBackup(updated: IvyWalletData): Either<ActionError, Unit> {
        TODO("Update Backup to Google Drive")
    }
}