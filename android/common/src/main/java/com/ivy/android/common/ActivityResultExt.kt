package com.ivy.android.common

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.simpleActivityForResultLauncher(
    intent: Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<Unit> {
    return activityForResultLauncher(
        createIntent = { _, _ -> intent },
        onActivityResult = onActivityResult
    )
}

fun Fragment.simpleActivityForResultLauncher(
    intent: Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<Unit> {
    return activityForResultLauncher(
        createIntent = { _, _ -> intent },
        onActivityResult = onActivityResult
    )
}

fun <I> AppCompatActivity.activityForResultLauncher(
    createIntent: (context: Context, input: I) -> Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<I> {
    return registerForActivityResult(
        activityResultContract(
            createIntent = createIntent,
            onActivityResult = onActivityResult
        )
    ) {
    }
}

fun <I> Fragment.activityForResultLauncher(
    createIntent: (context: Context, input: I) -> Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<I> {
    return registerForActivityResult(
        activityResultContract(
            createIntent = createIntent,
            onActivityResult = onActivityResult
        )
    ) {
    }
}

private fun <I> activityResultContract(
    createIntent: (context: Context, input: I) -> Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultContract<I, Unit> {
    return object : ActivityResultContract<I, Unit>() {
        override fun createIntent(
            context: Context,
            input: I
        ): Intent {
            return createIntent(context, input)
        }

        override fun parseResult(
            resultCode: Int,
            intent: Intent?
        ) {
            onActivityResult(resultCode, intent)
        }
    }
}