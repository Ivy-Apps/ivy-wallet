package com.ivy.releases

sealed interface ReleasesState {
    data class Success(val releasesInfo: ReleaseInfo) : ReleasesState
    data class Loading(val loadingMessage: String) : ReleasesState
    data class Error(val errorMessage: String) : ReleasesState
}
