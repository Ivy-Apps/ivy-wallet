package com.ivy.design.api

import com.ivy.design.IvyContext
import com.ivy.design.l0_system.IvyColors
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography
import com.ivy.design.l0_system.Theme

interface IvyDesign {
    fun context(): IvyContext

    fun typography(): IvyTypography

    fun colors(theme: Theme, isDarkModeEnabled: Boolean): IvyColors

    fun shapes(): IvyShapes
}
