package com.ivy.navigation.util

import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.stringArg(key: String): String = arg(key = key, type = string()) { it }
fun NavBackStackEntry.optionalStringArg(key: String): String? =
    optionalArg(key = key, type = string()) { it }

fun <ArgPrimitive, Arg> NavBackStackEntry.arg(
    key: String,
    type: NavBackStackEntry.(String) -> ArgPrimitive?,
    transform: (ArgPrimitive) -> Arg
): Arg =
    optionalArg(key = key, type = type, transform = transform) ?: error("missing '$key' argument")

fun <ArgPrimitive, Arg> NavBackStackEntry.optionalArg(
    key: String,
    type: NavBackStackEntry.(String) -> ArgPrimitive?,
    transform: (ArgPrimitive) -> Arg
): Arg? = type(key)?.let(transform)

fun string(): NavBackStackEntry.(String) -> String? = { key -> arguments?.getString(key) }
fun int(): NavBackStackEntry.(String) -> Int? = { key -> arguments?.getString(key)?.toIntOrNull() }
fun bool(): NavBackStackEntry.(String) -> Boolean? = { key -> arguments?.getBoolean(key) }