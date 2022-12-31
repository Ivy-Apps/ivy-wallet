package com.ivy.transaction.pure

import com.ivy.common.Quadruple
import com.ivy.common.isNotNullOrBlank
import com.ivy.data.transaction.Transaction

private const val MAX_SUGGESTIONS = 10

fun suggestTitle(
    transactions: List<Transaction>,
    title: String?,
): List<String> {
    val inputQuery = searchQuery(title) ?: ""

    return transactions.asSequence() // improve performance
        .filter { it.title.isNotNullOrBlank() }
        .groupBy { searchQuery(it.title) }
        .map { (trnQuery, trns) ->
            Triple(trnQuery, trns.size, trns.first())
        }.map { (trnQuery, trnsCount, trn) ->
            val exactMatch = if (inputQuery.isNotBlank())
                trnQuery?.contains(inputQuery) ?: false
            else false
            Quadruple(
                exactMatch, trnQuery, trnsCount, trn
            )
        }.sortedWith(
            compareByDescending<Quadruple<Boolean, String?, Int, Transaction>> { (exactMatch, _, _, _) ->
                // show exact matches first
                if (exactMatch) 1 else 0
            }.thenByDescending { (_, trnsCount, _, _) ->
                trnsCount
            }
        )
        .mapNotNull { (_, _, _, trn) ->
            // return the original transaction's title
            trn.title
        }
        .filter { suggestedTitle ->
            // don't show duplicated suggestions
            suggestedTitle != title
        }
        .toList()
        .take(MAX_SUGGESTIONS)
}

fun searchQuery(query: String?): String? =
    query?.trim()?.lowercase()?.takeIf { it.isNotBlank() }