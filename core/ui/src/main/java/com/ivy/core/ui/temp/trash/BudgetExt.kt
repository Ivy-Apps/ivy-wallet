package com.ivy.core.ui.temp.trash

import com.ivy.base.R
import com.ivy.core.ui.temp.stringRes
import com.ivy.data.Budget
import java.util.*

object BudgetExt {
    fun serialize(ids: List<UUID>): String {
        return ids.joinToString(separator = ",")
    }

    fun type(categoriesCount: Int): String {
        return when (categoriesCount) {
            0 -> stringRes(R.string.total_budget)
            1 -> stringRes(R.string.category_budget)
            else -> stringRes(
                R.string.multi_category_budget,
                categoriesCount.toString()
            )
        }
    }
}


fun Budget.parseCategoryIds(): List<UUID> {
    return parseIdsString(categoryIdsSerialized)
}

fun Budget.parseAccountIds(): List<UUID> {
    return parseIdsString(accountIdsSerialized)
}

private fun Budget.parseIdsString(idsString: String?): List<UUID> {
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


fun Budget.validate(): Boolean {
    return name.isNotEmpty() && amount > 0.0
}
