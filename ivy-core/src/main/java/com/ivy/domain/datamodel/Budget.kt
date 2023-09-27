package com.ivy.domain.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.persistence.db.entity.BudgetEntity
import java.util.UUID

@Immutable
data class Budget(
    val name: String,
    val amount: Double,

    val categoryIdsSerialized: String?,
    val accountIdsSerialized: String?,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val orderId: Double,
    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): BudgetEntity = BudgetEntity(
        name = name,
        amount = amount,
        categoryIdsSerialized = categoryIdsSerialized,
        accountIdsSerialized = accountIdsSerialized,
        isSynced = isSynced,
        isDeleted = isDeleted,
        orderId = orderId,
        id = id,
    )

    companion object {
        fun serialize(ids: List<UUID>): String {
            return ids.joinToString(separator = ",")
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
