package com.ivy.transaction

import androidx.compose.runtime.Immutable
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.Tag
import com.ivy.data.model.TagId
import com.ivy.legacy.data.EditTransactionDisplayLoan
import com.ivy.legacy.datamodel.Account
import com.ivy.wallet.domain.data.CustomExchangeRateState
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import java.time.Instant
import java.time.LocalDateTime

@Immutable
data class EditTransactionViewState(
    val transactionType: TransactionType,
    val initialTitle: String?,
    val titleSuggestions: ImmutableSet<String>,
    val currency: String,
    val description: String?,
    val dateTime: Instant?,
    val dueDate: Instant?,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>,
    val account: Account?,
    val toAccount: Account?,
    val category: Category?,
    val amount: Double,
    val hasChanges: Boolean,
    val displayLoanHelper: EditTransactionDisplayLoan,
    val backgroundProcessingStarted: Boolean,
    val customExchangeRateState: CustomExchangeRateState,
    val tags: ImmutableList<Tag>,
    val transactionAssociatedTags: ImmutableList<TagId>
)

sealed interface EditTransactionViewEvent {
    data class OnAmountChanged(val newAmount: Double) : EditTransactionViewEvent
    data class OnTitleChanged(val newTitle: String?) : EditTransactionViewEvent
    data class OnDescriptionChanged(val newDescription: String?) : EditTransactionViewEvent
    data class OnCategoryChanged(val newCategory: Category?) : EditTransactionViewEvent
    data class OnAccountChanged(val newAccount: Account) : EditTransactionViewEvent
    data class OnToAccountChanged(val newAccount: Account) : EditTransactionViewEvent
    data class OnDueDateChanged(val newDueDate: LocalDateTime?) : EditTransactionViewEvent
    data object OnChangeDate : EditTransactionViewEvent
    data object OnChangeTime : EditTransactionViewEvent
    data class OnSetTransactionType(val newTransactionType: TransactionType) :
        EditTransactionViewEvent

    data object OnPayPlannedPayment : EditTransactionViewEvent
    data object Delete : EditTransactionViewEvent
    data object Duplicate : EditTransactionViewEvent
    data class CreateCategory(val data: CreateCategoryData) : EditTransactionViewEvent
    data class EditCategory(val updatedCategory: Category) : EditTransactionViewEvent
    data class CreateAccount(val data: CreateAccountData) : EditTransactionViewEvent
    data class Save(val closeScreen: Boolean) : EditTransactionViewEvent
    data class SetHasChanges(val hasChangesValue: Boolean) : EditTransactionViewEvent
    data class UpdateExchangeRate(val exRate: Double?) : EditTransactionViewEvent

    sealed interface TagEvent : EditTransactionViewEvent {
        data class SaveTag(val name: String) : TagEvent
        data class OnTagSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDeSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDelete(val selectedTag: Tag) : TagEvent
        data class OnTagSearch(val query: String) : TagEvent
        data class OnTagEdit(val oldTag: Tag, val newTag: Tag) : TagEvent
    }
}
