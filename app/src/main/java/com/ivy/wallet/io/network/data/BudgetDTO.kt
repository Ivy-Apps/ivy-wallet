package com.ivy.wallet.io.network.data

import com.ivy.wallet.io.persistence.data.BudgetEntity
import java.util.*

data class BudgetDTO(
    val name: String,
    val amount: Double,

    val categoryIdsSerialized: String?,
    val accountIdsSerialized: String?,

    val orderId: Double,
    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): BudgetEntity = BudgetEntity(
        name = name,
        amount = amount,
        categoryIdsSerialized = categoryIdsSerialized,
        accountIdsSerialized = accountIdsSerialized,
        orderId = orderId,
        id = id,
        isSynced = true,
        isDeleted = false
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