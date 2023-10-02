package com.ivy.transaction

import com.ivy.base.model.TransactionType
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
    data class CreateCategory(val data: CreateCategoryData) : EditTransactionEvent
    data class EditCategory(val updatedCategory: Category) : EditTransactionEvent
    data class CreateAccount(val data: CreateAccountData) : EditTransactionEvent
    data class Save(val closeScreen: Boolean) : EditTransactionEvent
    data class SetHasChanges(val hasChangesValue: Boolean) : EditTransactionEvent
    data class UpdateExchangeRate(val exRate: Double?) : EditTransactionEvent
}