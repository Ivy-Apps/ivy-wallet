package com.ivy.data.model.testing

import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.TransactionId
import java.util.UUID

object ModelFixtures {
    val AccountId = AccountId(UUID.randomUUID())
    val CategoryId = CategoryId(UUID.randomUUID())
    val TransactionId = TransactionId(UUID.randomUUID())
}
