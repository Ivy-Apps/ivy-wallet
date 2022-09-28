package com.ivy.wallet.domain.deprecated.logic

import com.ivy.data.transaction.TransactionOld
import com.ivy.wallet.utils.capitalizeWords

@Deprecated("Use FP style, look into `domain.fp` package")
private fun List<TransactionOld>.extractUniqueTitles(
    excludeSuggestions: Set<String>? = null
): Set<String> {
    return this
        .filter { !it.title.isNullOrBlank() }
        .map { it.title!!.trim().capitalizeWords() }
        .filter { excludeSuggestions == null || !excludeSuggestions.contains(it) }
        .toSet()
}

@Deprecated("Use FP style, look into `domain.fp` package")
private suspend fun Set<String>.sortedByMostUsedFirst(countUses: suspend (String) -> Long): Set<String> {
    val titleCountMap = this
        .map {
            it to countUses(it)
        }
        .toMap()

    val sortedSuggestions = this
        .sortedByDescending {
            titleCountMap.getOrDefault(it, 0)
        }
        .toSet()

    return sortedSuggestions
}