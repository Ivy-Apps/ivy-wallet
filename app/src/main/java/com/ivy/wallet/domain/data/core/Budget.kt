package com.ivy.wallet.domain.data.core

import com.ivy.wallet.R
import com.ivy.wallet.io.network.data.BudgetDTO
import com.ivy.wallet.io.persistence.data.BudgetEntity
import com.ivy.wallet.stringRes
import java.util.*

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

    fun toDTO(): BudgetDTO = BudgetDTO(
        name = name,
        amount = amount,
        categoryIdsSerialized = categoryIdsSerialized,
        accountIdsSerialized = accountIdsSerialized,
        orderId = orderId,
        id = id
    )

    companion object {
        fun serialize(ids: List<UUID>): String {
            return ids.joinToString(separator = ",")
        }

        fun type(categoriesCount: Int): String {
            return when (categoriesCount) {
                0 -> stringRes(R.string.total_budget)
                1 -> stringRes(R.string.category_budget)
                else -> stringRes(R.string.multi_category_budget, categoriesCount.toString())
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