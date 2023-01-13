package com.ivy.photo.frame.action

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import arrow.core.Either
import arrow.core.computations.either
import com.ivy.core.domain.action.Action
import com.ivy.photo.frame.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AddFrameAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val uriToBitmapAct: UriToBitmapAct
) : Action<AddFrameAct.Input, Either<AddFrameError, Bitmap>>() {
    data class Input(
        val photoUri: Uri,
    )

    override suspend fun action(input: Input): Either<AddFrameError, Bitmap> = either {
        val photo = uriToBitmapAct(input.photoUri).mapLeft(AddFrameError::LoadPhoto).bind()
        val frame = loadFrameBitmap().bind()
        val resizedFrame = resizeFrame(frame, photo.width, photo.height).bind()
        addFrameToPhoto(photo, resizedFrame).bind()
    }

    private fun loadFrameBitmap(): Either<AddFrameError, Bitmap> =
        Either.catch(AddFrameError::LoadFrame) {
            BitmapFactory.decodeResource(
                appContext.resources,
                R.drawable.ivyframe
            )
        }

    private fun addFrameToPhoto(
        photo: Bitmap,
        resizedFrame: Bitmap
    ): Either<AddFrameError, Bitmap> =
        Either.catch(AddFrameError::AddFrameToPhoto) {
            val photoWidth = photo.width
            val photoHeight = photo.height

            val photoWithFrame = Bitmap.createBitmap(photoWidth, photoHeight, photo.config)
            val canvas = Canvas(photoWithFrame)
            canvas.drawBitmap(photo, Matrix(), null) // draw photo
            canvas.drawBitmap(resizedFrame, 0f, 0f, null) // draw frame

            photoWithFrame
        }

    private fun resizeFrame(
        frame: Bitmap, photoWidth: Int, photoHeight: Int
    ): Either<AddFrameError, Bitmap> = Either.catch(AddFrameError::ResizeFrame) {
        Bitmap.createScaledBitmap(frame, photoWidth, photoHeight, true)
    }
}


sealed interface AddFrameError {
    data class LoadPhoto(val reason: Throwable) : AddFrameError
    data class LoadFrame(val reason: Throwable) : AddFrameError
    data class ResizeFrame(val reason: Throwable) : AddFrameError
    data class AddFrameToPhoto(val reason: Throwable) : AddFrameError
}