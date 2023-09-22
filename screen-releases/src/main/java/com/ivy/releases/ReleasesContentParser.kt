package com.ivy.releases

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ReleasesContentParser @Inject constructor() {
    fun toCustomList(text: String?): ImmutableList<String> {
        if (text.isNullOrBlank()) return persistentListOf()
        val list = text.split("\n")
        val customList = mutableListOf<String>()

        for (commit in list) {
            val new = commit.drop(2)
            customList.add(new)
        }

        return customList.toImmutableList()
    }

    fun toCustomDate(date: String): String {
        return date.dropLast(10)
    }
}