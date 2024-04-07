package com.ivy.data.model.testing

import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.positiveDouble
import io.kotest.property.arbitrary.uuid

fun Arb.Companion.income() = arbitrary {
    Income(
        id = TransactionId(value = Arb.uuid().bind()),
        title = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        description = Arb.maybe(Arb.notBlankTrimmedString()).bind(),
        category = CategoryId(),
        time =,
        settled = false,
        metadata = TransactionMetadata(
            recurringRuleId = null,
            loanId = null,
            loanRecordId = null
        ),
        lastUpdated =,
        removed = false,
        tags = listOf(),
        value = Value(
            amount = PositiveDouble(
                value = 0.0
            ), asset = AssetCode(code = "")
        ),
        account = AccountId(value =)

    )
}