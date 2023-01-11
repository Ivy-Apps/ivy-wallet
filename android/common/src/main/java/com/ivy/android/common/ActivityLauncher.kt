package com.ivy.android.common

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

abstract class ActivityLauncher<Input, Output> {
    private lateinit var launcher: ActivityResultLauncher<Input>
    private lateinit var internalCallback: (resultCode: Int, data: Intent?) -> Unit

    protected abstract fun intent(context: Context, input: Input): Intent
    protected abstract fun onActivityResult(resultCode: Int, intent: Intent?): Output

    fun wire(activity: AppCompatActivity) {
        launcher = activity.activityForResultLauncher(
            createIntent = ::intent
        ) { resultCode, intent ->
            internalCallback(resultCode, intent)
        }
    }

    fun launch(input: Input, onResult: (Output) -> Unit) {
        internalCallback = { resultCode, intent ->
            onResult(onActivityResult(resultCode, intent))
        }
        launcher.launch(input)
    }
}