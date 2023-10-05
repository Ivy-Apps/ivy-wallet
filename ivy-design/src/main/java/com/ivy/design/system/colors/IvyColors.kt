package com.ivy.design.system.colors

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
object IvyColors {
    val White = Color(0xFFFAFAFC)
    val ExtraLightGray = Color(0xFFEBEBF0)
    val LightGray = Color(0xFFCBCBD6)
    val Gray = Color(0xFF74747A)
    val DarkGray = Color(0xFF303033)
    val ExtraDarkGray = Color(0xFF1C1C1F)
    val Black = Color(0xFF09090A)

    val Red = ColorShades(
        extraLight = Color(0xFFF5ABAB),
        light = Color(0xFFF5AB87),
        kindaLight = Color(0xFFF56262),
        primary = Color(0xFFF53D3D),
        kindaDark = Color(0xFFCC3333),
        dark = Color(0xFF8F2424),
        extraDark = Color(0xFF521414),
    )
    val Orange = ColorShades(
        extraLight = Color(0xFFF5C4AB),
        light = Color(0xFFF5AB87),
        kindaLight = Color(0xFFF59362),
        primary = Color(0xFFF57A3D),
        kindaDark = Color(0xFFCC6633),
        dark = Color(0xFF8F4724),
        extraDark = Color(0xFF522914),
    )
    val Yellow = ColorShades(
        extraLight = Color(0xFFF5E9AB),
        light = Color(0xFFF5E287),
        kindaLight = Color(0xFFF5DC62),
        primary = Color(0xFFF5D018),
        kindaDark = Color(0xFFCCAD14),
        dark = Color(0xFFA38B10),
        extraDark = Color(0xFF7A680C),
    )
    val Green = ColorShades(
        extraLight = Color(0xFFABF5DC),
        light = Color(0xFF5AE0B4),
        kindaLight = Color(0xFF38E0A8),
        primary = Color(0xFF12B880),
        kindaDark = Color(0xFF10A372),
        dark = Color(0xFF0C7A56),
        extraDark = Color(0xFF085239),
    )
    val Blue = ColorShades(
        extraLight = Color(0xFFABD0F5),
        light = Color(0xFF87BEF5),
        kindaLight = Color(0xFF62ABF5),
        primary = Color(0xFF3193F5),
        kindaDark = Color(0xFF3380CC),
        dark = Color(0xFF24598F),
        extraDark = Color(0xFF153352),
    )
    val Purple = ColorShades(
        extraLight = Color(0xFFB8ABF5),
        light = Color(0xFF9987F5),
        kindaLight = Color(0xFF7B62F5),
        primary = Color(0xFF5C3DF5),
        kindaDark = Color(0xFF4D33CC),
        dark = Color(0xFF36248F),
        extraDark = Color(0xFF1F1452),
    )
    val Pink = ColorShades(
        extraLight = Color(0xFFF5ABD0),
        light = Color(0xFFF587BE),
        kindaLight = Color(0xFFF562AB),
        primary = Color(0xFFF53D99),
        kindaDark = Color(0xFFCC3380),
        dark = Color(0xFF8F2459),
        extraDark = Color(0xFF521433),
    )
}

