package com.ivy.parser.common

import com.ivy.parser.Parser
import com.ivy.parser.sat

fun letter(): Parser<Char> = sat { it.isLetter() }