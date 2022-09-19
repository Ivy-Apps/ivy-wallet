package com.ivy.core.domain.action.transaction

import com.ivy.common.toUUID
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.tag.TagDao
import com.ivy.core.persistence.dao.trn.TrnMetadataDao
import com.ivy.core.persistence.dao.trn.TrnTagDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.tag.TagEntity
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.TrnQueryExecutor
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

/**
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
) : FlowAction<TrnQuery, List<Transaction>>() {

    override fun TrnQuery.createFlow(): Flow<List<Transaction>> =
        combine(accountsFlow(), categoriesFlow(), trnsSignal.receive()) { accs, cats, _ ->
            val entities = queryExecutor.query(this.toTrnWhere())

            val accsMap = accs.associateBy { it.id }
            val catsMap = cats.associateBy { it.id }

            combine(
                entities.map {
                    mapTransactionEntityFlow(
                        accounts = accsMap,
                        categories = catsMap,
                        trn = it
                    )
                }
            ) { trns ->
                trns.toList()
            }
        }.flattenMerge()
            .flowOn(Dispatchers.Default)

    private fun mapTransactionEntityFlow(
        accounts: Map<UUID, Account>,
        categories: Map<UUID, Category>,
        trn: TrnEntity,
    ): Flow<Transaction> {
        val account = accounts[trn.accountId.toUUID()] ?: return flow {}

        val trnId = trn.id
        val tagsFlow = trnTagDao.findByTrnId(trnId = trnId).flatMapMerge { trnTags ->
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
                sync = trn.sync,
                metadata = mapMetadata(metadataEntities),
                attachments = mapAttachments(attachmentEntities),
                tags = mapTags(tagEntities),
            )
        }
    }

    private fun trnTime(entity: TrnEntity): TrnTime {
        // TODO: Check dateTime conversion correctness
        val localeTime = LocalDateTime.ofInstant(entity.time, ZoneId.systemDefault())
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
            sync = it.sync
        )
    }

    private fun mapTags(entities: List<TagEntity>): List<Tag> = entities.map {
        Tag(
            id = it.id,
            color = it.color,
            name = it.name,
            orderNum = it.orderNum,
            state = it.state,
            sync = it.sync
        )
    }
}