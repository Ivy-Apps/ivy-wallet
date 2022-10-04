package com.ivy.common.time

import io.kotest.core.spec.style.StringSpec

class LocalUtcTimeConversionTest : StringSpec({
    "local <> utc" {
        val local = timeNow()
    }
})