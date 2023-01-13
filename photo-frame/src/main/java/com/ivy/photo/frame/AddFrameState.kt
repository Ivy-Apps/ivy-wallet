package com.ivy.photo.frame

import android.graphics.Bitmap
import com.ivy.photo.frame.data.MessageUi

data class AddFrameState(
    val photoWithFrame: Bitmap?,
    val message: MessageUi,
)