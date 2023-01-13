package com.ivy.photo.frame

import android.net.Uri

sealed interface AddFrameEvent {
    data class PhotoChanged(val photoUri: Uri) : AddFrameEvent
    data class SavePhoto(val location: Uri) : AddFrameEvent
}