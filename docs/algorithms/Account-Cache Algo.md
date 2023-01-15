# "Account-Cache" Algo

An algorithm for efficiently calculating account's `RawStats`: `Income` and `Expense` using caching.

## Algorithm

![Account-Cache-Diagram](../../assets/account_cache_algo.svg)
**[--> View the diagram full-screen <--](https://raw.githubusercontent.com/Ivy-Apps/ivy-wallet/develop/assets/account_cache_algo.svg)**

The purpose of the "Account-Cache" algorithm is to optimze the way the account's balance (and overall) in Ivy Wallet is calculated.

To calculate the balance of an account Ivy Wallet goes through all transactions of that account and executes the [Calc Algo](./Calc%20Algo.md).

> Account's Balance = $\Sigma$(of all incomes _in account's currency_) - $\Sigma$(of all expenses _in account's currency_)

_This algorithm must iterate through all transactions to calculate the balance and that becomes inefficient when you use the app for a few years and have 3k+ transactions. But we can do better than that!_


## Invalidate cache algorithm

![Invalidate-Account-Cache-Diagram](../../assets/account_cache_invalidate_algo.svg)
**[--> View the diagram full-screen <--](https://raw.githubusercontent.com/Ivy-Apps/ivy-wallet/develop/assets/account_cache_invalidate_algo.svg)**

### 0. Introducing the cache 

We must persist the last calculated `RawStats` for that account because it's independent of the exchange rates.

_For example, if my account has $10 and €10, we can't simply store $20.82 because when any of the USD or EUR exchange rate changes it'll because invalid._

**"accounts_cache" DB table**
- account_id: `String` (PK)
- incomes_json: `String`, JSON {"EUR":12;0, USD":13.0}
- expenses_json: `String`, JSON {"EUR":12;0, USD":13.0}
- incomes_count: Int
- expenses_count: Int

JSON string for the incomes and expenses is preffered because having another table will just create overhead and complexity.

> Room DB is preferred over the DatStore (key-value pairs) because some users might have many accounts and SQLite is more optimized for that purpose.

### 1. Lookup for a cache entry for the account? `O(log(# of accounts)) time | O(1) space`

> _Note: All Kotlin code in pseudo-code._

```kotlin
// SELECT * from accounts_cache WHERE accountId = ? LIMIT 1;
val cacheEntry = accountCacheDao.findByAccountId(accountId)
```

_Complexity: In practice `# of account cache entries` = `# of accounts`. The `account_id` column in the SQLite table is indexed and [SQLite uses a B-Tree for the indexes](https://www.sqlitetutorial.net/sqlite-index) => it takes O(logn) time to find cache entry for the account._


### [CACHE NOT FOUND] 1. Fetch all transactions for the account `O(log(# of all trns) + # of trns for the account) time | O(# of trns for the account) space`

```kotlin
// SELECT ... FROM transactions WHERE accountId = ?
calcTrnDao.findAllByAccountId(accountId)
```

_Complexity: The `accountId` column is indexed and it takes SQLite `O(log(# of all trns)) time` to find the row ids for the transactions with the target accountId - search in a B-Tree. Then it takes `O(# of trns for the account) time` to iterate them. This operation only allocates new memory for the account's transactions - `O(# of trns for the account) space`._

### [CACHE NOT FOUND] 2. Execute [`RawStats` from Calc Algo](Calc%20Algo.md) `O(# of all account's trns) time | O(# of unique  currencies in the account) space`

Iterates through all transactions and sums their incomes and expenses by currency.

### [CACHE NOT FOUND] 3. Update the account's cache `O(# account cache entries) time | O(1) space`

```kotlin
// UPSERT
accountCacheDao.save()
```

> It might take `O(# account cache entries) time` if the B-Tree indexes needs to be re-balanced.

---

### [CACHED] 1. Fetch account's transactions only after the cache time `O(log(?) time | O(?) space`

> Transaction's `trnTime` column is indexed.

```kotlin
// SELECT ... FROM transactions WHERE accountId = ? AND time = ?
caclTrnsDao.findAllByAccountAndAfter(accountId, cacheTime)
```

_Complexity: Depends on the SQLite engine strategy, cannot be estimated._


### [CACHED] 2. Execute [`RawStats` from Calc Algo](Calc%20Algo.md) `O(# of account trns only after cache's time) time | O(# of unique  currencies in the account) space`

### [CACHED] 3. Sum the cached values by the newly fetched ones `O(# of unique currencies) space-time`

[CACHED] 4. Update the account's cache `O(# account cache entries) time | O(1) space`

## Complexity

The overall complexity of the algorithm is

**Best-case (cache exists)**
- Lookup cache: `O(log(# of accounts)) time | O(1) space`
- Trns for account only after cache time: `O(?) space-time` _(depends on SQLite optimizer)_
- Raw Stats: `O(# of account trns only after cache's time) time | O(# of unique  currencies in the account) space`
- Sum the cached values + the newly fetched ones: `O(# of unique currencies) space-time`
- Update the cache: `O(# account cache entries) time | O(1) space`
- **Overall time: `O(log(# of all accounts) + # of account's trns after cache time + # of unique currencies in acc + # of account cache entries)`**
- **Overall space: `O(? but no more than worst-case)`**

> **Practical time: `O(# of account's trns after cache time)`**

> **Practical space: `O(# of account's trns after cache time)`**

**Worst-case (no cache)**
- Lookup cache: `O(log(# of accounts)) time | O(1) space`
- All trns for account: `O(log(# of all trns) + # of trns for the account) time | O(# of trns for the account) space`
- Raw Stats: `O(# of all account's trns) time | O(# of unique  currencies in the account) space`
- Update the cache: `O(# of account cache entries) time | O(1) space`
- **Overall time: `O(log(# of accounts) + log(# of all trns) + # of trns for the account + # of account cache entries)`**
- **Overall space: `O(# of trns by "account_id" + # of unique currencies in the account)`**

> **Practical time: `O(# of trns by "account_id" + log(# of all trns))`**

> **Practical space: `O(# of trns by "account_id")`**