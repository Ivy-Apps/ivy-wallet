package com.ivy.core.domain.action.transaction.transfer

import com.ivy.common.toNonEmptyList
import com.ivy.common.toUUID
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsByQueryAct
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnPurpose
import javax.inject.Inject

class TransferByBatchIdAct @Inject constructor(
    private val trnsByQueryAct: TrnsByQueryAct,
    private val trnLinkRecordDao: TrnLinkRecordDao,
) : Action<String, TrnListItem.Transfer?>() {
    override suspend fun String.willDo(): TrnListItem.Transfer? {
        val trnIds = trnLinkRecordDao.findByBatchId(batchId = this)
            .map { it.trnId }
        if (trnIds.isEmpty()) return null
        val trns = trnsByQueryAct(
            TrnQuery.ByIdIn(
                trnIds.map { it.toUUID() }.toNonEmptyList()
            )
        )

        val from = trns.firstOrNull { it.purpose == TrnPurpose.TransferFrom } ?: return null
        val to = trns.firstOrNull { it.purpose == TrnPurpose.TransferTo } ?: return null
        val fee = trns.firstOrNull { it.purpose == TrnPurpose.Fee }

        return TrnListItem.Transfer(
            batchId = this,
            time = from.time,
            from = from,
            to = to,
            fee = fee,
        )
    }
}