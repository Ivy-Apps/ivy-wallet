package com.ivy.data.model.testing

import com.ivy.data.model.AccountId
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid

fun Arb.Companion.accountId(): Arb<AccountId> = Arb.uuid().map(::AccountId)
