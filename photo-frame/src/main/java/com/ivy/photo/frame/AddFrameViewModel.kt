package com.ivy.photo.frame

import arrow.core.Either
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.photo.frame.action.AddFrameAct
import com.ivy.photo.frame.action.SavePhotoAct
import com.ivy.photo.frame.data.MessageUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AddFrameViewModel @Inject constructor(
    private val addFrameAct: AddFrameAct,
    private val savePhotoAct: SavePhotoAct,
) : SimpleFlowViewModel<AddFrameState, AddFrameEvent>() {
    override val initialUi = AddFrameState(
        photoWithFrame = null,
        message = MessageUi.None,
    )

    private val photoWithFrame = MutableStateFlow(initialUi.photoWithFrame)
    private val message = MutableStateFlow(initialUi.message)

    override val uiFlow: Flow<AddFrameState> = combine(
        photoWithFrame,
        message
    ) { photoWithFrame, message ->
        AddFrameState(
            photoWithFrame = photoWithFrame,
            message = message,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: AddFrameEvent) {
        when (event) {
            is AddFrameEvent.PhotoChanged -> handlePhotoChanged(event)
            is AddFrameEvent.SavePhoto -> handleSavePhoto(event)
        }
    }

    private suspend fun handlePhotoChanged(event: AddFrameEvent.PhotoChanged) {
        message.value = MessageUi.Loading("Adding frame to photo...")
        when (val res = addFrameAct(AddFrameAct.Input(photoUri = event.photoUri))) {
            is Either.Left -> {
                message.value = MessageUi.Error(res.value.toString())
            }
            is Either.Right -> {
                photoWithFrame.value = res.value
                message.value = MessageUi.None
            }
        }
    }

    private suspend fun handleSavePhoto(event: AddFrameEvent.SavePhoto) {
        val photo = photoWithFrame.value ?: return

        message.value = MessageUi.Loading("Saving photo...")
        when (val res = savePhotoAct(
            SavePhotoAct.Input(
                photo = photo,
                location = event.location,
            )
        )) {
            is Either.Left -> {
                message.value = MessageUi.Error(res.value.toString())
            }
            is Either.Right -> {
                message.value = MessageUi.Success("Photo saved! Location: ${event.location.path}")
            }
        }
    }
    // endregion
}