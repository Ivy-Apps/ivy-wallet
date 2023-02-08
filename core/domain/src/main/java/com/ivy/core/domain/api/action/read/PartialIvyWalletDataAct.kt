package com.ivy.core.domain.api.action.read

import arrow.core.Either
import com.ivy.core.data.sync.PartialIvyWalletData
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.data.ActionError
import com.ivy.core.domain.calculation.syncDataFrom
import com.ivy.core.persistence.api.account.AccountRead
import com.ivy.core.persistence.api.attachment.AttachmentRead
import com.ivy.core.persistence.api.budget.BudgetRead
import com.ivy.core.persistence.api.category.CategoryRead
import com.ivy.core.persistence.api.recurring.RecurringRuleRead
import com.ivy.core.persistence.api.saving.SavingGoalRead
import com.ivy.core.persistence.api.saving.SavingGoalRecordRead
import com.ivy.core.persistence.api.tag.TagRead
import com.ivy.core.persistence.api.transaction.TransactionRead
import javax.inject.Inject

class PartialIvyWalletDataAct @Inject constructor(
    private val accountRead: AccountRead,
    private val transactionRead: TransactionRead,
    private val categoryRead: CategoryRead,
    private val tagRead: TagRead,
    private val recurringRuleRead: RecurringRuleRead,
    private val attachmentRead: AttachmentRead,
    private val budgetRead: BudgetRead,
    private val savingGoalRead: SavingGoalRead,
    private val savingGoalRecordRead: SavingGoalRecordRead,
) : Action<Unit, Either<ActionError, PartialIvyWalletData>>() {
    override suspend fun action(input: Unit): Either<ActionError, PartialIvyWalletData> =
        Either.catch {
            PartialIvyWalletData(
                accounts = syncDataFrom(accountRead.allPartial()),
                transactions = syncDataFrom(transactionRead.allPartial()),
                categories = syncDataFrom(categoryRead.allPartial()),
                tags = syncDataFrom(tagRead.allPartial()),
                recurringRules = syncDataFrom(recurringRuleRead.allPartial()),
                attachments = syncDataFrom(attachmentRead.allPartial()),
                budgets = syncDataFrom(budgetRead.allPartial()),
                savingGoals = syncDataFrom(savingGoalRead.allPartial()),
                savingGoalRecords = syncDataFrom(savingGoalRecordRead.allPartial())
            )
        }.mapLeft(ActionError::IO)
}