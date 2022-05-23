package com.ivy.wallet.ui.experiment.images

import com.ivy.frp.monad.Res
import com.ivy.frp.then
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.viewmodel.experiment.FetchImagesAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val fetchImagesAct: FetchImagesAct
) : FRPViewModel<ImagesState, ImagesEvent>() {
    override val _state: MutableStateFlow<ImagesState> =
        MutableStateFlow(ImagesState.Loading)

    override suspend fun handleEvent(event: ImagesEvent): suspend () -> ImagesState = when (event) {
        is ImagesEvent.LoadImages -> loadImages()
    }

    private suspend fun loadImages() = suspend {
        updateState { ImagesState.Loading }
        Unit
    } then fetchImagesAct then {
        when (it) {
            is Res.Err -> ImagesState.Error(it.error.message ?: "idk")
            is Res.Ok -> ImagesState.Success(it.data)
        }
    }
}