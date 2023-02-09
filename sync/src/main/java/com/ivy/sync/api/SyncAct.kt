package com.ivy.sync.api

import arrow.core.Either
import arrow.core.continuations.either
import com.ivy.backup.base.WriteIvyWalletDataAct
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.action.read.IvyWalletDataFromPartialAct
import com.ivy.core.domain.api.action.read.PartialIvyWalletDataAct
import com.ivy.core.domain.api.data.ActionError
import com.ivy.sync.action.RemoteBackupSource
import com.ivy.sync.calculation.applyDiff
import com.ivy.sync.calculation.calculateDiff
import javax.inject.Inject

class SyncAct @Inject constructor(
    private val remoteBackupSource: RemoteBackupSource,
    private val partialIvyWalletDataAct: PartialIvyWalletDataAct,
    private val ivyWalletDataFromPartialAct: IvyWalletDataFromPartialAct,
    private val writeIvyWalletDataAct: WriteIvyWalletDataAct,
) : Action<Unit, Either<ActionError, Unit>>() {
    /**
     * Eventual consistency:
     * If any of the steps fail, nothing gets corrupted.
     * On the next sync (if successful) remote and local should converge.
     */
    override suspend fun action(input: Unit): Either<ActionError, Unit> = either {
        // 1. Fetch the entire Backup JSON from Drive (action)
        val completeRemoteBackup = remoteBackupSource.fetchBackup().bind()
        // 2. Retrieve only id, removed, lastUpdated from Local DB (action)
        val partialLocalDb = partialIvyWalletDataAct(Unit).bind()
        // 3. Calculate diffs (calculation)
        val diff = calculateDiff(
            remote = completeRemoteBackup,
            localPartial = partialLocalDb,
        )

        // 4. Retrieve complete local diff: SELECT *; not only their ids (action)
        val completeLocalDiff = ivyWalletDataFromPartialAct(diff.remotePartial).bind()
        // 5. Update the remote backup with the local items (calculation)
        val updatedRemoteBackup = completeRemoteBackup.applyDiff(completeLocalDiff)
        // 6. Upload the updated remote backup (action)
        remoteBackupSource.uploadBackup(updatedRemoteBackup).bind()

        // 7. Update the Local DB with the remote diff (action)
        writeIvyWalletDataAct(diff.local).bind()
    }
}