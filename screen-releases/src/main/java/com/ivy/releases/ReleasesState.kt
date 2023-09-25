package com.ivy.releases

import kotlinx.collections.immutable.ImmutableList

sealed interface ReleasesState {
    data class Success(val releasesInfo: ImmutableList<ReleaseInfo>) : ReleasesState
    data class Loading(val loadingMessage: String) : ReleasesState
    data class Error(val errorMessage: String) : ReleasesState
}
