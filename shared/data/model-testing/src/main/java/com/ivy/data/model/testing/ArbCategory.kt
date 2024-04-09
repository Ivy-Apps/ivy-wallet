package com.ivy.data.model.testing

import com.ivy.data.model.CategoryId
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid

fun Arb.Companion.categoryId(): Arb<CategoryId> = Arb.uuid().map(::CategoryId)