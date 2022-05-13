package com.ivy.wallet.ui.experiment.images

sealed class ImagesState {
    object Loading : ImagesState()

    data class Error(val errMsg: String) : ImagesState()

    data class Success(val urls: List<String>) : ImagesState()
}