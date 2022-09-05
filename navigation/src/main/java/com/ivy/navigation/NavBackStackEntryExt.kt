package com.ivy.navigation

import androidx.navigation.NavBackStackEntry

fun <T> NavBackStackEntry.arg(key: String, transform: (String) -> T): T =
    arguments?.getString(key)?.let(transform) ?: error("missing '$key' argument")

fun <T> NavBackStackEntry.optionalArg(key: String, transform: (String) -> T): T? =
    arguments?.getString(key)?.let(transform)