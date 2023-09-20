package com.ivy.contributors

import kotlinx.collections.immutable.ImmutableList

data class ContributorsState(
    val contributors: ContributorsStage
)

sealed interface ContributorsStage {
    data object Loading : ContributorsStage
    data class Success(val contributors: ImmutableList<Contributor>) : ContributorsStage
    data class Error(val errorMessage: String) : ContributorsStage
}

