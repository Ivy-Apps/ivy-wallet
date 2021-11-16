package com.ivy.wallet.logic

import com.ivy.wallet.base.capitalizeWords
import com.ivy.wallet.base.isNotNullOrBlank
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.TransactionDao
import java.util.*

class SmartTitleSuggestionsLogic(
    private val transactionDao: TransactionDao
) {

    fun suggest(
        title: String?,
        categoryId: UUID?,
        accountId: UUID?
    ): Set<String> {
        val suggestions = mutableSetOf<String>()

        if (title != null && title.isNotEmpty()) {
            //suggest by title
            val suggestionsByTitle = transactionDao.findAllByTitleMatchingPattern("${title}%")
                .extractUniqueTitles()
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPattern("${it}%")
                }

            suggestions.addAll(suggestionsByTitle)
        }

        if (categoryId != null) {
            //suggest by category
            //all titles used for the specific category
            //ordered by N times used

            val suggestionsByCategory = transactionDao
                .findAllByCategory(
                    categoryId = categoryId
                )
                .extractUniqueTitles()
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPatternAndCategoryId(
                        pattern = it,
                        categoryId = categoryId
                    )
                }

            suggestions.addAll(suggestionsByCategory)
        }


        if (suggestions.isEmpty() && accountId != null) {
            //last resort, suggest by account

            //all titles used for the specific account
            //ordered by N times used

            val suggestionsByAccount = transactionDao
                .findAllByAccount(
                    accountId = accountId
                )
                .extractUniqueTitles()
                .sortedByMostUsedFirst {
                    transactionDao.countByTitleMatchingPatternAndAccountId(
                        pattern = it,
                        accountId = accountId
                    )
                }

            suggestions.addAll(suggestionsByAccount)
        }

        return suggestions
    }
}

private fun List<Transaction>.extractUniqueTitles(): Set<String> {
    return this.filter { it.title.isNotNullOrBlank() }
        .map { it.title!!.trim().capitalizeWords() }
        .toSet()
}

private fun Set<String>.sortedByMostUsedFirst(countUses: (String) -> Long): Set<String> {
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