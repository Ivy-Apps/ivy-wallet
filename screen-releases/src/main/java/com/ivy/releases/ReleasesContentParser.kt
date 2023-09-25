package com.ivy.releases

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ReleasesContentParser @Inject constructor() {
    fun toCommitsList(commits: String?): ImmutableList<String> {
        if (commits.isNullOrBlank()) return persistentListOf()
        val commitsList = commits.split("\n")

        return commitsList.map { commit ->
            // remove " -"
            commit.drop(2).trim()
        }.toImmutableList()
    }

    fun toReleaseDate(date: String): String {
        // e.g. transforms original "2023-09-16T17:42:08Z" into "2023-09-16"
        return date.dropLast(10)
    }
}