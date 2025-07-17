package com.ivy.releases

import kotlinx.collections.immutable.ImmutableList

data class ReleaseInfo(
    val releaseName: String,
    val releaseUrl: String,
    val releaseDate: String,
    val releaseCommits: ImmutableList<String>
)
