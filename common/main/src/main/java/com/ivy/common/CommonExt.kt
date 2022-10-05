package com.ivy.common

import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)