package com.ivy.contributors

import kotlinx.collections.immutable.ImmutableList

data class ContributorsState(
    val projectResponse: ProjectResponse,
    val contributorsResponse: ContributorsResponse
)

sealed interface ProjectResponse {
    data object Loading : ProjectResponse
    data class Success(val projectInfo: ProjectRepositoryInfo) : ProjectResponse
    data object Error : ProjectResponse
}

sealed interface ContributorsResponse {
    data object Loading : ContributorsResponse
    data class Success(val contributors: ImmutableList<Contributor>) : ContributorsResponse
    data class Error(val errorMessage: String) : ContributorsResponse
}