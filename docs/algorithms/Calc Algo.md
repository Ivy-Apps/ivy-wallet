# Calc Algo

Calculates Income and Expense values in a given currency provided a list of transactions.

This algorithm is behind almost all numbers that you see in Ivy Wallet.

![calc_algo.svg](../../assets/calc_algo.svg)

## Algorithm

- Input: `[CalcTrn]`
```kotlin
data class CalcTrn(
    val amount: Double,
    val currency: Currency,
    val type: TransactionType,
) 
```
- Output: `RawStats`
```kotlin
data class RawStats(
    val incomes: Map<Currency, Double>,
    val expenses: Map<Currency, Double>,
    val incomesCount: Int,
    val expensesCount: Int,
)
```

### Steps

_(pseudo-code)_

**A) Raw Stats:** O(# of trns) time | O(# of unique  currencies) space `[RawStatsFlow]` `[can be a pure function]`

1. Initialization: `O(1) space-time`
```kotlin
val incomes = mutableMapOf<Currency, Double>()
val expenses = mutableMapOf<Currency, Double>()
var incomesCount = 0
var expensesCount = 0
```

2. Loop through transactions and aggregate: `O(# of trns) time | O(# of unique currencies) space`
```kotlin
trns.forEach {
    when(type) {
        Income -> {
            incomesCount++
            incomesMap[it.currency] += it.amount
        }
        Expense -> {
            expensesCount++
            expenses[it.currency] += it.amount
        }
    }
}
```


**B) Get the exchange rates** O(# of rates * # of overriden rates) time | O(# of rates + # of overriden rates) space `✨base-currency` `✨rates` `✨overriden-rates`

> RX: `✨X` means reacts to X

1. Retrieve the base currency from the DataStore: `O(1) space-time`
```kotlin
DataStore<Preferences>.preferrences.map { it[key] }
```

2. Retrieve the automatic exchange rates from Room DB `O(# of rates) time | O(# of rates for the base currency) space` `✨base-currency`
```kotlin
@Query("SELECT currency, amount FROM exchange_rates WHERE baseCurrency = :baseCurrency")
    fun findAllByBaseCurrency(baseCurrency: String): Flow<List<Rate>>
```

3. Retrieve the manually overriden exchange rates from Room DB `O(# of overidden rates) space-time` `✨base-currnecy`

4. Replace automatic rates with the overriden ones `O(# of rates * # of overriden rates) time | O(# of rates + # of overriden rates) space` `✨rates` `✨overriden-rates`
```kotlin
combine(rates, overridenRates) {
    val res = mutableMapOf<Currency, Double>()
    rates.forEach {
        res[it.key] = it.value
    }
    overridenRates.forEach {
        res[it.key] = it.value
    }
    res
}
```

**C) Exchange RawStats**



## Complexity