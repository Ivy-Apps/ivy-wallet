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
