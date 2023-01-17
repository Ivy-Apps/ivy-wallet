package com.ivy.core.domain.algorithm.accountcache

import arrow.core.None
import arrow.core.Some
import com.ivy.core.domain.algorithm.calc.data.RawStats
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class AccountCacheMapperTest : FreeSpec({
    // TODO: Fix: disabled because JSONObject is Android object and Stub! is provided for unit tests
    "serialize <> deserialize account cache".config(enabled = false) - {
        val rawStatsGen = arbitrary {
            val mapGen = Arb.map(
                keyArb = Arb.string(3),
                valueArb = Arb.double(min = 0.0),
            )
            RawStats(
                incomes = mapGen.bind(),
                expenses = mapGen.bind(),
                incomesCount = Arb.int(min = 0).bind(),
                expensesCount = Arb.int(min = 0).bind(),
                newestTrnTime = Arb.instant().bind(),
            )
        }
        checkAll(
            rawStatsGen,
            Arb.uuid(),
        ) { originalStats, accountId ->
            val cache = rawStatsToAccountCache(
                accountId = accountId.toString(),
                rawStats = originalStats,
            )

            when (val res = accountCacheToRawStats(cache)) {
                None -> fail("Parsing of $cache failed!")
                is Some -> res.value
            }
        }
    }
})