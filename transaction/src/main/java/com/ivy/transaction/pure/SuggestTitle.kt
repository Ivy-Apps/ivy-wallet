package com.ivy.transaction.pure

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import java.util.*

/**
 * 1) Display exact title matches
 * 2) Display the ones from the same category
 */
fun suggestTitle(
    transactions: List<Transaction>,
    title: String?,
    accountId: UUID,
    categoryId: UUID?,
    trnType: TransactionType,
): List<String> {
    val titleQuery = searchQuery(title)

    transactions.filter { it.title != null }
        .groupBy {
            searchQuery(it.title)
        }
        .mapNotNull { (normalizedTitle, trns) ->
            if (normalizedTitle != null && trns.isNotEmpty()) {
                Triple(normalizedTitle, trns.size, trns.first())
            } else null
        }

//    val exactMatch = if (titleQuery != null)
//        transactionsWithTitle.filter {
//            val trnTitle = it.title ?: return@filter false
//            searchQuery(trnTitle)?.contains(titleQuery) ?: false
//        }.mapNotNull {
//            it.title
//        } else emptyList()
//
//    val categoryMatch = transactionsWithTitle.filter {
//        it.category?.id == categoryId
//    }

    TODO()
}

fun searchQuery(query: String?): String? =
    query?.trim()?.lowercase()?.takeIf { it.isNotBlank() }