package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.domain.data.core.Budget
import java.util.*

@Entity(tableName = "budgets")
data class BudgetEntity(
    val name: String,
    val amount: Double,

    val categoryIdsSerialized: String?,
    val accountIdsSerialized: String?,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val orderId: Double,
    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Budget = Budget(
        name = name,
        amount = amount,
        categoryIdsSerialized = categoryIdsSerialized,
        accountIdsSerialized = accountIdsSerialized,
        isSynced = isSynced,
        isDeleted = isDeleted,
        orderId = orderId,
        id = id
    )

    companion object {
        fun serialize(ids: List<UUID>): String {
            return ids.joinToString(separator = ",")
        }

        fun type(categoriesCount: Int): String {
            return when (categoriesCount) {
                0 -> "Total Budget"
                1 -> "Category Budget"
                else -> "Multi-Category ($categoriesCount) Budget"
            }
        }
    }

    fun parseCategoryIds(): List<UUID> {
        return parseIdsString(categoryIdsSerialized)
    }

    fun parseAccountIds(): List<UUID> {
        return parseIdsString(accountIdsSerialized)
    }

    private fun parseIdsString(idsString: String?): List<UUID> {
        return try {
            if (idsString == null) return emptyList()

            idsString
                .split(",")
                .map { UUID.fromString(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    fun validate(): Boolean {
        return name.isNotEmpty() && amount > 0.0
    }
}