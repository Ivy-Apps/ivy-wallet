package com.ivy.sync.api

import arrow.core.Either
import arrow.core.continuations.either
import com.ivy.backup.base.WriteIvyWalletDataAct
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.action.read.IvyWalletDataFromPartialAct
import com.ivy.core.domain.api.action.read.PartialIvyWalletDataAct
import com.ivy.core.domain.api.data.ActionError
import com.ivy.sync.action.RemoteBackupSource
import com.ivy.sync.calculation.merge
import com.ivy.sync.calculation.updatedRemoteBackup
import javax.inject.Inject

class SyncAct @Inject constructor(
    private val remoteBackupSource: RemoteBackupSource,
    private val partialIvyWalletDataAct: PartialIvyWalletDataAct,
    private val ivyWalletDataFromPartialAct: IvyWalletDataFromPartialAct,
    private val writeIvyWalletDataAct: WriteIvyWalletDataAct,
) : Action<Unit, Either<ActionError, Unit>>() {
    override suspend fun action(input: Unit): Either<ActionError, Unit> = either {
        val remote = remoteBackupSource.fetchBackup().bind()
        val merged = merge(
            remote = remote,
            localPartial = partialIvyWalletDataAct(Unit).bind()
        )
        val updatedRemote = updatedRemoteBackup(
            remote = remote,
            newerLocal = ivyWalletDataFromPartialAct(merged.remoteToUpdate).bind()
        )
        remoteBackupSource.uploadBackup(updatedRemote).bind() // update remote
        writeIvyWalletDataAct(merged.localToUpdate).bind() // update locally
    }
}