package com.ivy.core.domain.api.action.read

import arrow.core.Either
import com.ivy.core.data.*
import com.ivy.core.data.sync.*
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.data.ActionError
import com.ivy.core.persistence.api.ReadSyncable
import com.ivy.core.persistence.api.account.AccountRead
import com.ivy.core.persistence.api.attachment.AttachmentRead
import com.ivy.core.persistence.api.budget.BudgetRead
import com.ivy.core.persistence.api.category.CategoryRead
import com.ivy.core.persistence.api.recurring.RecurringRuleRead
import com.ivy.core.persistence.api.saving.SavingGoalRead
import com.ivy.core.persistence.api.saving.SavingGoalRecordRead
import com.ivy.core.persistence.api.tag.TagRead
import com.ivy.core.persistence.api.transaction.TransactionRead
import java.util.*
import javax.inject.Inject

class IvyWalletDataFromPartialAct @Inject constructor(
    private val accountRead: AccountRead,
    private val transactionRead: TransactionRead,
    private val categoryRead: CategoryRead,
    private val tagRead: TagRead,
    private val recurringRuleRead: RecurringRuleRead,
    private val attachmentRead: AttachmentRead,
    private val budgetRead: BudgetRead,
    private val savingGoalRead: SavingGoalRead,
    private val savingGoalRecordRead: SavingGoalRecordRead,
) : Action<PartialIvyWalletData, Either<ActionError, IvyWalletData>>() {
    override suspend fun action(input: PartialIvyWalletData): Either<ActionError, IvyWalletData> =
        Either.catch {
            IvyWalletData(
                accounts = fromPartial(input.accounts, accountRead, ::AccountId),
                transactions = fromPartial(input.transactions, transactionRead, ::TransactionId),
                categories = fromPartial(input.categories, categoryRead, ::CategoryId),
                tags = fromPartial(input.tags, tagRead, ::TagId),
                recurringRules = fromPartial(
                    input.recurringRules, recurringRuleRead, ::RecurringRuleId
                ),
                attachments = fromPartial(input.attachments, attachmentRead, ::AttachmentId),
                budgets = fromPartial(input.budgets, budgetRead, ::BudgetId),
                savingGoals = fromPartial(input.savingGoals, savingGoalRead, ::SavingGoalId),
                savingGoalRecords = fromPartial(
                    input.savingGoalRecords, savingGoalRecordRead, ::SavingGoalRecordId
                ),
            )
        }.mapLeft(ActionError::IO)

    private suspend fun <T : Syncable, TID : UniqueId> fromPartial(
        partial: SyncData<Syncable>,
        read: ReadSyncable<T, TID, *>,
        mapId: (UUID) -> TID,
    ): SyncData<T> = SyncData(
        items = read.byIds(partial.items.map { mapId(it.id.uuid) }),
        deleted = partial.deleted,
    )
}