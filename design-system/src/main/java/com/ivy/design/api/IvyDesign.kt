package com.ivy.design.api

import androidx.compose.runtime.Stable
import com.ivy.design.Theme
import com.ivy.design.l0_system.IvyColors
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography

@Stable
interface IvyDesign {
    fun typography(): IvyTypography

    fun colors(theme: Theme, isSystemInDarkTheme: Boolean): IvyColors

    fun shapes(): IvyShapes
}