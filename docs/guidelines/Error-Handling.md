# Error Handling

It's common operations to fail and we should expect that.
In Ivy Wallet we **do not throw exceptions** but rather make functions that
can fail return [Either<Error, Data>](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/).

Either is a generic data type that models two possible case:
- `Either.Left` for the unhappy path (e.g. request failing, invalid inlut, no network connection)
- `Either.Right` for the happy path

Simplified, `Either` is just:

```kotlin
sealed interface Either<E, A> {
  data class Left<E>(val data: E): Either<E, Nothing>
  data class Right<T>(val data: A): Either<Nothing, A>
}

fun <E,A,B> Either<E, A>.fold(
  mapLeft: (E) -> B,
  mapRight: (A) -> B
): B = when(this) {
  Either.Left -> mapLeft(data)
  Either.Right -> mapRight(data)
}

// a bunch more extension functions and utils
```

So in Ivy, operations that can fail (logically or for some other reason) we'll model using **Either**.

## Data Layer example

Imagine that we're building a program that buys BTC if its price is below $50,000.

```kotlin
interface BtcDataSource {
  suspend fun fetchCurrentPriceUSD(): Either<String, PositiveDouble>
  suspend buy(amount: PositiveDouble): Either<BuyError, Unit>

  sealed interface BuyError {
    data class IO(val e: Throwable) : BuyError
    data object TooSmallAmount : BuyError
  }
}

interface MyBank {
  suspend fun currentblBalanceUSD(): Either<Unit, PositiveDouble>
}

class CryptoInvestor @Inject constructor(
  private val btcDataSource: BtcDataSource,
  private val myBank: MyBank
) {
  suspend buyIfCheap(): Either<String, PositiveDouble> = either {
    val btcPrice = btcDataSource.fetchCurrentPriceUSD().bind()
    // .bind() - if it fails returns Either.Left and short-circuits the function
    if(btcPrice.value > 50_000) {
      // short-circuits and returns Either.Left with the msg below
      raise("BTC is expensive! Won't buy.")
    }
    val myBalance = myBank.currentBalanceUSD().mapLeft {
      "Failed to fetch my bank account balance."
    }.bind()
    btcDataSource.buy(myBalance).mapLeft { err ->
       when(err) {
         is BuyError.IO -> "Failed to buy because of an IO error - ${e.msg}"
         BuyError.TooSmallAmount -> "Failed to buy because I'm poor."
       }
    }.bind() // maps the BuyError to String and short-circuits
    // Bought BTC with my entire balance!
    myBalance // <-- the last line returns the Either.Right
  }
}
```

Let's analyze, simplified:
- `either {}` puts us into a "special" scope where the last line returns `Either.Right` and also give us access to some functions:
  - `Operation.bind()`: if the operation fails terminates the `either {}` with operation's `Left` value, otherwise `.bind()` returns operation's `Right` value
  - `raise(E)`: like **throw** but for `either {}` - terminates the function with `Left(E)`
- `Either.mapLeft {}`: transforms the `Left` (error type) of the `Either`. In the example we do it so we can match the left type of the `either {}`

**Useful `Either` functions:**

- 
