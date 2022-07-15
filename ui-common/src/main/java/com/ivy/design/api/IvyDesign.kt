package com.ivy.design.api

import com.ivy.design.IvyContext
import com.ivy.design.l0_system.IvyColors
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography
import com.ivy.data.Theme

interface IvyDesign {
    fun context(): IvyContext

    fun typography(): IvyTypography

    fun colors(theme: com.ivy.data.Theme, isDarkModeEnabled: Boolean): IvyColors

    fun shapes(): IvyShapes
}