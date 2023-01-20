package com.ivy.core.domain.action.transaction

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.toUUID
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.domain.pure.util.combineList
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.tag.TagDao
import com.ivy.core.persistence.dao.trn.TrnMetadataDao
import com.ivy.core.persistence.dao.trn.TrnTagDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.tag.TagEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.*
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

/**
 * Note: Deleted but not synced transactions aren't returned.
 * @return a flow of domain **[[Transaction]]** by a given query.
 * ## Query
 *
 * ### Filters
 * - [TrnQuery.ByAccountId]
 * - [TrnQuery.ByCategoryId]
 * - [TrnQuery.ByType]
 * - [TrnQuery.ActualBetween]
 * - [TrnQuery.DueBetween]
 * - [TrnQuery.ById]
 * - see [TrnQuery]
 *
 * ### Building more complex query:
 * - and()
 * - or()
 * - not()
 * - brackets()
 */
@Deprecated("don't use it! It's inefficient.")
@OptIn(FlowPreview::class)
class TrnsFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val categoriesFlow: CategoriesFlow,
    private val queryExecutor: TrnQueryExecutor,
    private val trnMetadataDao: TrnMetadataDao,
    private val attachmentDao: AttachmentDao,
    private val trnTagDao: TrnTagDao,
    private val tagDao: TagDao,
    private val trnsSignal: TrnsSignal,
    private val timeProvider: TimeProvider,
) : FlowAction<TrnQuery, List<Transaction>>() {

    override fun createFlow(input: TrnQuery): Flow<List<Transaction>> = combine(
        accountsFlow(), categoriesFlow(), trnsSignal.receive()
    ) { accs, cats, _ ->
        val dbQuery = brackets(input.toTrnWhere()) and not(TrnWhere.BySync(SyncState.Deleting))
        val entities = queryExecutor.query(dbQuery)
        if (entities.isEmpty()) {
            return@combine flowOf(emptyList())
        }

        val accsMap = accs.associateBy { it.id }
        val catsMap = cats.associateBy { it.id }

        combineList(
            entities.mapNotNull {
                mapTransactionEntityFlow(
                    accounts = accsMap,
                    categories = catsMap,
                    trn = it
                )
            }
        )
    }.flattenLatest()
        .flowOn(Dispatchers.Default)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun mapTransactionEntityFlow(
        accounts: Map<UUID, Account>,
        categories: Map<UUID, Category>,
        trn: TransactionEntity,
    ): Flow<Transaction>? {
        val account = accounts[trn.accountId.toUUID()]
            ?: return null

        val trnId = trn.id
        val tagsFlow = trnTagDao.findByTrnId(trnId = trnId)
            .flatMapLatest { trnTags ->
                tagDao.findByTagIds(tagIds = trnTags.map { it.tagId })
            }

        return combine(
            trnMetadataDao.findByTrnId(trnId = trnId),
            tagsFlow,
            attachmentDao.findByAssociatedId(associatedId = trnId)
        ) { metadataEntities, tagEntities, attachmentEntities ->
            Transaction(
                id = trnId.toUUID(),
                account = account,
                type = trn.type,
                value = Value(
                    amount = trn.amount,
                    currency = trn.currency
                ),
                category = categories[trn.categoryId?.toUUID()],
                time = trnTime(trn),
                title = trn.title.takeIf { !it.isNullOrBlank() },
                description = trn.description.takeIf { !it.isNullOrBlank() },
                state = trn.state,
                purpose = trn.purpose,
                sync = Sync(
                    state = trn.sync,
                    lastUpdated = trn.lastUpdated.toLocal(timeProvider)
                ),
                metadata = mapMetadata(metadataEntities),
                attachments = mapAttachments(attachmentEntities),
                tags = mapTags(tagEntities),
            )
        }
    }

    private fun trnTime(entity: TransactionEntity): TrnTime {
        val localeTime = entity.time.toLocal(timeProvider)
        return when (entity.timeType) {
            TrnTimeType.Actual -> TrnTime.Actual(localeTime)
            TrnTimeType.Due -> TrnTime.Due(localeTime)
        }
    }

    private fun mapMetadata(metadataEntities: List<TrnMetadataEntity>): TrnMetadata {
        val metadata = metadataEntities.associate { it.key to it.value.toUUID() }
        return TrnMetadata(
            recurringRuleId = metadata[TrnMetadata.RECURRING_RULE_ID],
            loanRecordId = metadata[TrnMetadata.LOAN_RECORD_ID],
            loanId = metadata[TrnMetadata.LOAN_ID]
        )
    }

    private fun mapAttachments(entities: List<AttachmentEntity>): List<Attachment> = entities.map {
        Attachment(
            id = it.id,
            associatedId = it.id,
            uri = it.id,
            source = it.source,
            filename = it.filename,
            type = it.type,
            sync = Sync(
                state = it.sync,
                lastUpdated = it.lastUpdated.toLocal(timeProvider)
            )
        )
    }

    private fun mapTags(entities: List<TagEntity>): List<Tag> = entities.map {
        Tag(
            id = it.id,
            color = it.color,
            name = it.name,
            orderNum = it.orderNum,
            state = it.state,
            sync = Sync(
                state = it.sync,
                lastUpdated = it.lastUpdated.toLocal(timeProvider)
            )
        )
    }
}