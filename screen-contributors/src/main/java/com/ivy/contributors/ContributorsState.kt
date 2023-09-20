package com.ivy.contributors

import kotlinx.collections.immutable.ImmutableList

sealed interface ContributorsState {
    data object Loading : ContributorsState
    data class Success(val contributors: ImmutableList<Contributor>) : ContributorsState
    data class Error(val errorMessage: String) : ContributorsState
}

