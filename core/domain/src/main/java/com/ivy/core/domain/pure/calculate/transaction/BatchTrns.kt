package com.ivy.core.domain.pure.calculate.transaction

import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.transaction.*

/**
 * Groups transactions together into a list of [TrnListItem.Transfer] and [TrnListItem.Trn]
 * based on the provided [links] and domain batching rules.
 * Resulting list **order isn't guaranteed**.
 *
 * @return an unordered list of [TrnListItem]
 */
internal fun batchTrns(
    trns: List<Transaction>,
    links: List<TrnLinkRecordEntity>
): List<TrnListItem> {
    val batchedTrns = processTrnBatches(
        trns = trns,
        links = links
    )

    val batchedTrnIds = batchedTrns.mapNotNull {
        when (it) {
            is TrnListItem.DateDivider -> null
            is TrnListItem.Transfer -> listOfNotNull(it.from.id, it.to.id, it.fee?.id)
            is TrnListItem.Trn -> listOf(it.trn.id)
        }
    }.flatten()

    val nonBatchedTrns = trns.filter { !batchedTrnIds.contains(it.id) }
        .map { TrnListItem.Trn(it) }

    return nonBatchedTrns + batchedTrns
}

private fun processTrnBatches(
    trns: List<Transaction>,
    links: List<TrnLinkRecordEntity>,
): List<TrnListItem> = links.groupBy { it.batchId }
    .mapNotNull { (batchId, links) ->
        val batchedTrnsIds = links.map { it.trnId }
        val batchedTrns = trns
            .filter { batchedTrnsIds.contains(it.id.toString()) }
            .takeIf { it.isNotEmpty() } ?: return@mapNotNull null

        TrnBatch(
            batchId = batchId,
            trns = batchedTrns
        )
    }.mapNotNull(::mapToDomain)


private fun mapToDomain(trnBatch: TrnBatch): TrnListItem? =
    // We can add more domain types derived from batch later
    recognizeTransfer(trnBatch)

private fun recognizeTransfer(trnBatch: TrnBatch): TrnListItem.Transfer? {
    val trns = trnBatch.trns
    if (trns.size != 2 && trns.size != 3) return null

    val from = trns.firstOrNull {
        it.type == TransactionType.Expense && it.purpose == TrnPurpose.TransferFrom
    } ?: return null
    val to = trns.firstOrNull {
        it.type == TransactionType.Income && it.purpose == TrnPurpose.TransferTo
    } ?: return null
    val fee = trns.firstOrNull {
        it.type == TransactionType.Expense && it.purpose == TrnPurpose.Fee
    }

    return TrnListItem.Transfer(
        batchId = trnBatch.batchId,
        time = from.time,
        from = from,
        to = to,
        fee = fee,
    )
}