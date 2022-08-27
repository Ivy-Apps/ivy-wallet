package com.ivy.wallet.utils

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.ivy.frp.view.navigation.onScreenStart
import kotlin.math.roundToInt


@Composable
fun keyboardVisibleState(): State<Boolean> {
    val rootView = LocalView.current

    val keyboardVisible = remember {
        mutableStateOf(false)
    }

    onScreenStart {
        rootView.addKeyboardListener {
            keyboardVisible.value = it
        }
    }

    return keyboardVisible
}

fun View.addKeyboardListener(keyboardCallback: (visible: Boolean) -> Unit) {
    doOnLayout {
        //get init state of keyboard
        var keyboardVisible = isKeyboardOpen(this)

        //callback as soon as the layout is set with whether the keyboard is open or not
        keyboardCallback(keyboardVisible)

        //whenever the layout resizes/changes, callback with the state of the keyboard.
        viewTreeObserver.addOnGlobalLayoutListener {
            val keyboardUpdateCheck = isKeyboardOpen(this)
            //since the observer is hit quite often, only callback when there is a change.
            if (keyboardUpdateCheck != keyboardVisible) {
                keyboardCallback(keyboardUpdateCheck)
                keyboardVisible = keyboardUpdateCheck
            }
        }
    }
}


fun isKeyboardOpen(rootView: View): Boolean {
    return try {
        WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
            .isVisible(WindowInsetsCompat.Type.ime())
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun convertDpToPixel(context: Context, dp: Float): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertDpToPixel(context: Context, dp: Int): Int {
    return convertDpToPixel(context, dp.toFloat()).roundToInt()
}

@SuppressLint("ComposableNaming")
@Composable
fun setStatusBarDarkTextCompat(darkText: Boolean) {
    setStatusBarDarkTextCompat(
        view = LocalView.current,
        darkText = darkText
    )
}

fun setStatusBarDarkTextCompat(view: View, darkText: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view.windowInsetsController?.setStatusBarDarkText(darkText)
    } else {
        val window = (view.context as Activity).window
        setStatusBarDarkTextOld(window, darkText)
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun WindowInsetsController.setStatusBarDarkText(darkText: Boolean) {
    setSystemBarsAppearance(
        if (darkText) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
    )
}

@Suppress("DEPRECATION")
fun setStatusBarDarkTextOld(window: Window, darkText: Boolean) {
    window.decorView.systemUiVisibility = if (darkText) {
        window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // set status bar dark text
    } else {
        window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // reset status bar dark text
    }
}

@RequiresApi(api = Build.VERSION_CODES.M)
fun setSystemBarTheme(pActivity: Activity, pIsDark: Boolean) {
    // Fetch the current flags.
    val lFlags = pActivity.window.decorView.systemUiVisibility
    // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
    pActivity.window.decorView.systemUiVisibility =
        if (pIsDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}


fun lerp(start: Int, end: Int, @FloatRange(from = 0.0, to = 1.0) fraction: Float): Int {
    return ((start + fraction * (end - start)).roundToInt());
}

fun lerp(start: Float, end: Float, @FloatRange(from = 0.0, to = 1.0) fraction: Float): Float {
    return (start + fraction * (end - start))
}

fun lerp(start: Double, end: Double, @FloatRange(from = 0.0, to = 1.0) fraction: Double): Double {
    return (start + fraction * (end - start))
}

fun colorLerp(start: Color, end: Color, fraction: Float): Color {
    return Color(ArgbEvaluator().evaluate(fraction, start.toArgb(), end.toArgb()) as Int)
}

fun hideKeyboard(view: View) {
    try {
        val imm: InputMethodManager =
            view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (ignore: Exception) {
    }
}

/*
 * Creating a Bitmap of view with ARGB_8888.
 *
*/
fun captureView(view: View): Bitmap? {
    return try {
        val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.Transparent.toArgb())
        }
        view.draw(canvas)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Bitmap.blur(
    context: Context,
    blurRadius: Float = 7.5f,
    bitmapScale: Float = 0.4f
): Bitmap {
    val width = (this.width * bitmapScale).roundToInt()
    val height = (this.height * bitmapScale).roundToInt()
    val inputBitmap: Bitmap = Bitmap.createScaledBitmap(this, width, height, false)
    val outputBitmap: Bitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(blurRadius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}

fun postDelayed(delayMs: Long, run: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ run() }, delayMs)
}

fun post(run: () -> Unit) {
    Handler(Looper.getMainLooper()).post { run() }
}

fun showKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.setMargin(
    topDp: Int? = null,
    bottomDp: Int? = null,
    leftDp: Int? = null,
    rightDp: Int? = null,
) {
    val lp = layoutParams as ViewGroup.MarginLayoutParams
    if (topDp != null) {
        lp.topMargin = convertDpToPixel(context, topDp)
    }
    if (bottomDp != null) {
        lp.bottomMargin = convertDpToPixel(context, bottomDp)
    }
    if (leftDp != null) {
        lp.leftMargin = convertDpToPixel(context, leftDp)
    }
    if (rightDp != null) {
        lp.rightMargin = convertDpToPixel(context, rightDp)
    }
    layoutParams = lp
    requestLayout()
    invalidate()
}