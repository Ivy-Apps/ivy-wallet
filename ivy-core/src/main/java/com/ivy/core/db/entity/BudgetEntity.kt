package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.Budget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Keep
@Serializable
@Entity(tableName = "budgets")
data class BudgetEntity(
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: Double,

    @SerialName("categoryIdsSerialized")
    val categoryIdsSerialized: String?,
    @SerialName("accountIdsSerialized")
    val accountIdsSerialized: String?,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @SerialName("orderId")
    val orderId: Double,
    @PrimaryKey
    @SerialName("id")
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
