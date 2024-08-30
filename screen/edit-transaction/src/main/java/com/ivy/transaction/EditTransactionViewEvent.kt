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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Immutable
data class EditTransactionViewState(
    val transactionType: TransactionType,
    val initialTitle: String?,
    val titleSuggestions: ImmutableSet<String>,
    val currency: String,
    val description: String?,
    val dateTime: LocalDateTime?,
    val dueDate: LocalDateTime?,
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

sealed interface EditTransactionEvent {
    data class OnAmountChanged(val newAmount: Double) : EditTransactionEvent
    data class OnTitleChanged(val newTitle: String?) : EditTransactionEvent
    data class OnDescriptionChanged(val newDescription: String?) : EditTransactionEvent
    data class OnCategoryChanged(val newCategory: Category?) : EditTransactionEvent
    data class OnAccountChanged(val newAccount: Account) : EditTransactionEvent
    data class OnToAccountChanged(val newAccount: Account) : EditTransactionEvent
    data class OnDueDateChanged(val newDueDate: LocalDateTime?) : EditTransactionEvent
    data class OnSetDateTime(val newDateTime: LocalDateTime) : EditTransactionEvent
    data class OnSetDate(val newDate: LocalDate) : EditTransactionEvent
    data class OnSetTime(val newTime: LocalTime) : EditTransactionEvent
    data class OnSetTransactionType(val newTransactionType: TransactionType) : EditTransactionEvent
    data object OnPayPlannedPayment : EditTransactionEvent
    data object Delete : EditTransactionEvent
    data object Duplicate : EditTransactionEvent
    data class CreateCategory(val data: CreateCategoryData) : EditTransactionEvent
    data class EditCategory(val updatedCategory: Category) : EditTransactionEvent
    data class CreateAccount(val data: CreateAccountData) : EditTransactionEvent
    data class Save(val closeScreen: Boolean) : EditTransactionEvent
    data class SetHasChanges(val hasChangesValue: Boolean) : EditTransactionEvent
    data class UpdateExchangeRate(val exRate: Double?) : EditTransactionEvent

    sealed interface TagEvent : EditTransactionEvent {
        data class SaveTag(val name: String) : TagEvent
        data class OnTagSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDeSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDelete(val selectedTag: Tag) : TagEvent
        data class OnTagSearch(val query: String) : TagEvent
        data class OnTagEdit(val oldTag: Tag, val newTag: Tag) : TagEvent
    }
}
