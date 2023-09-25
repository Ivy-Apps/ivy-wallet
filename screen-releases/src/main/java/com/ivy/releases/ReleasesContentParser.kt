package com.ivy.releases

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ReleasesContentParser @Inject constructor() {
    fun toCommitsList(text: String?): ImmutableList<String> {
        if (text.isNullOrBlank()) return persistentListOf()
        val list = text.split("\n")
        val commitsList = mutableListOf<String>()

        for (commit in list) {
            val transformedCommit = commit.drop(2)
            commitsList.add(transformedCommit)
        }

        return commitsList.toImmutableList()
    }

    fun toReleaseDate(date: String): String {
        // e.g. transforms original "2023-09-16T17:42:08Z" into "2023-09-16"
        return date.dropLast(10)
    }
}