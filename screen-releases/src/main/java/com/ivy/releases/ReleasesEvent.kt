package com.ivy.releases

sealed interface ReleasesEvent {
    data object OnTryAgainClicked : ReleasesEvent
}