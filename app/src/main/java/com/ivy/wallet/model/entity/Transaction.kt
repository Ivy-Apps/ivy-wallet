package com.ivy.wallet.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.TransactionType
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "transactions")
data class Transaction(
    val accountId: UUID,
    val type: TransactionType,
    val amount: Double,
    val toAccountId: UUID? = null,
    val toAmount: Double? = null,
    val title: String? = null,
    val description: String? = null,
    val dateTime: LocalDateTime? = null,
    val categoryId: UUID? = null,
    val dueDate: LocalDateTime? = null,

    val recurringRuleId: UUID? = null,

    val attachmentUrl: String? = null,

    //This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,

    //SaltEdge integration -------
    val seTransactionId: String? = null,
    val seAutoCategoryId: UUID? = null,
    //SaltEdge integration -------

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) : TransactionHistoryItem {

    fun smartCategoryId(): UUID? {
        return categoryId ?: seAutoCategoryId
    }

    fun isIdenticalWith(transaction: Transaction?): Boolean {
        if (transaction == null) return false

        //Set isSynced && isDeleted to false so they aren't accounted in the equals check
        return this.copy(
            isSynced = false,
            isDeleted = false
        ) == transaction.copy(
            isSynced = false,
            isDeleted = false
        )
    }
}