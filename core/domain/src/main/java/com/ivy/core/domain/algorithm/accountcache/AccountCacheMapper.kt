package com.ivy.core.domain.algorithm.accountcache

import arrow.core.Option
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import com.ivy.data.CurrencyCode
import org.json.JSONObject

fun rawStatsToAccountCache(
    accountId: String,
    rawStats: RawStats,
): AccountCacheEntity {
    fun mapToJson(map: Map<CurrencyCode, Double>): String {
        val json = JSONObject()
        map.forEach { (currency, amount) ->
            json.put(currency, amount)
        }
        return json.toString()
    }

    return AccountCacheEntity(
        accountId = accountId,
        incomesJson = mapToJson(rawStats.incomes),
        expensesJson = mapToJson(rawStats.expenses),
        incomesCount = rawStats.incomesCount,
        expensesCount = rawStats.expensesCount,
        timestamp = rawStats.newestTrnTime,
    )
}

fun accountCacheToRawStats(cache: AccountCacheEntity): Option<RawStats> {
    fun jsonToMap(jsonString: String): Map<CurrencyCode, Double> {
        val json = JSONObject(jsonString)
        val map = mutableMapOf<CurrencyCode, Double>()
        json.keys().forEach { key ->
            map[key] = json.getDouble(key)
        }
        return map
    }

    return Option.catch {
        RawStats(
            incomes = jsonToMap(cache.incomesJson),
            expenses = jsonToMap(cache.expensesJson),
            incomesCount = cache.incomesCount,
            expensesCount = cache.expensesCount,
            newestTrnTime = cache.timestamp
        )
    }
}