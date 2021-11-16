package com.ivy.wallet.logic

import com.ivy.wallet.base.capitalizeWords
import com.ivy.wallet.base.isNotNullOrBlank
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
                .filter { it.title.isNotNullOrBlank() }
                .map { it.title!!.trim().capitalizeWords() }
                .toSet()

            suggestions.addAll(suggestionsByTitle)
        }

        if (categoryId != null) {
            //suggest by category
            //all titles used for the specific category
            //ordered by N times used

            val categoryTitles = transactionDao
                .findAllByCategory(
                    categoryId = categoryId
                )
                .filter { it.title.isNotNullOrBlank() }
                .map { it.title!!.trim().capitalizeWords() }
                .toSet()

            val titleCountMap = categoryTitles
                .map {
                    it to transactionDao.countByTitleMatchingPatternAndCategoryId(
                        pattern = it,
                        categoryId = categoryId
                    )
                }
                .toMap()

            val suggestionsByCategory = categoryTitles
                .sortedByDescending {
                    titleCountMap.getOrDefault(it, 0)
                }
                .toSet()

            suggestions.addAll(suggestionsByCategory)
        }


        if (suggestions.isEmpty() && accountId != null) {
            //last resort, suggest by account

            //all titles used for the specific account
            //ordered by N times used

            val accountTitles = transactionDao
                .findAllByAccount(
                    accountId = accountId
                )
                .filter { it.title.isNotNullOrBlank() }
                .map { it.title!!.trim().capitalizeWords() }
                .toSet()

            val titleCountMap = accountTitles
                .map {
                    it to transactionDao.countByTitleMatchingPatternAndAccountId(
                        pattern = it,
                        accountId = accountId
                    )
                }
                .toMap()

            val suggestionsByAccount = accountTitles
                .sortedByDescending {
                    titleCountMap.getOrDefault(it, 0)
                }
                .toSet()

            suggestions.addAll(suggestionsByAccount)
        }

        return suggestions
    }
}