package com.ivy.contributors

import kotlinx.collections.immutable.ImmutableList

data class ContributorsState(
    val contributors: ImmutableList<Contributor>?
)
