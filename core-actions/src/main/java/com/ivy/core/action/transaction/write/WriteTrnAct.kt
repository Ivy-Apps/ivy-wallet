package com.ivy.core.action.transaction.write

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import com.ivy.sync.transaction.SyncTrnAct
import com.ivy.sync.transaction.mark
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteTrnAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val syncTrnAct: SyncTrnAct
) : FPAction<IOEffect<Transaction>, SyncTask>() {

    override suspend fun IOEffect<Transaction>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Save -> {
                persist(
                    item.mark(
                        isSynced = false,
                        isDeleted = false
                    )
                )
            }
            is IOEffect.Delete -> {
                persist(
                    item.mark(
                        isSynced = false,
                        isDeleted = true
                    )
                )
            }
        }

        syncTaskFrom(this asParamTo syncTrnAct)
    }

    private suspend fun persist(trn: Transaction) =
        transactionDao.save(mapToEntity(trn))
}

/** How you can use it?
```
suspend fun demo(trn: Transaction, writeTrnAct: WriteTrnAct) {
writeTrnAct(IOEffect.Delete(trn)).sync()

val syncTask = writeTrnAct(IOEffect.Save(trn))
syncTask.sync()
}
```
 */