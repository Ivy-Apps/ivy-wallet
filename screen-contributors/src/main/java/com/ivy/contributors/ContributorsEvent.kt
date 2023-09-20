package com.ivy.contributors

sealed interface ContributorsEvent {
    data object TryAgainButtonClicked : ContributorsEvent
}