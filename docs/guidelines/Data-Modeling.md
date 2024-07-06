# Data Modeling

The way your data is modeled is crucial to the complexity of your software. 
We say that a data model `A` is better than a data model `B` <=> `A` eliminates more impossible cases than `B`. Let me show you what that means in practice.

## Algebraic Data Types

**Screen example**

Imagine that you have to implement a screen with three states - loading, success, and error.
A naive way to model its UI state could be:

```kotlin
data class ScreenUiState(
  val content: String?,
  val loading: Boolean,
  val error: String?
)
```

The problem with this approach is that our code will have to deal with many impossible (illegal) states. For example:

- What to show if `loading = false`, `content = null`, `error = null`?
- What to do if we have both `loading = true` and `error != null`?

There are so many ways things can go wrong - for example, a common one is forgetting to reset `loading` back to `false`.
A better way to model this would be to use [Algebraic Data types (ADTs)](https://wiki.haskell.org/Algebraic_data_type) or, simply said in Kotlin: `data classes`, `sealed interfaces`, and combinations of both.

```kotlin
sealed interface ScreenUiState {
  data object Loading : ScreenUiState
  data class Content(val text: String) : ScreenUiState
  data class Error(val msg: String) : ScreenUiState
}
```

With the ADTs representation, we eliminate all impossible cases. We also eliminate that at compile-time, meaning that whatever shit we do - the compiler will never allow the code to run.

**Takeaway:** Model your data using `data classes`, and  `sealed interfaces` (and combinations of them) in a way that:

- Mirrors your domain one-to-one, exactly and explicitly.
- Impossible cases are eliminated by construction and at compile-time.

> You can have `sealed interfaces` (**sum** types) inside `data classes` (**product** types) and vice-versa. Combine them as you see fit.

## Explicit Types

Unfortunately, not all impossible cases can be eliminated at compile time. For example, imagine that we're building
a food ordering app and want to model its domain. A naive data model could be:

```kotlin
data class Order(
  val id: UUID,
  val userId: UUID,
  val itemId: UUID,
  val count: Int,
  val time: LocalDateTime,
  val trackingId: String,
)
```

I'm making this up, but the goal is to demonstrate common mistakes and how to fix them.
Do you spot them?

Let's think and analyze:

1. What if someone orders a `count = 0` or even worse a `count = -1`?
2. Imagine a function `placeOrder(orderId: UUID, userId: UUID, itemId: UUID, ...)`. How likely is someone to pass a wrong `UUID` and mess UUIDs up?
3. The `trackingId` seems to be required and important but what if someone passes `trackingId = ""` or `trackingId = "XYZ  "`?

I can go on, but you see the point. So let's discuss how we can fix it.

```kotlin
data class Order(
  val id: OrderId,
  val user: UserId,
  val item: ItemId,
  val count: PositiveInt,
  val time: Instant, // <-- always in UTC 
  val trackingId: NotBlankTrimmedString
)

@JvmInline
value class OrderId(val value: UUID)

@JvmInline
value class UserId(val value: UUID)

@JvmInline
value class ItemId(val value: UUID)

@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object : Exact<Int, PositiveInt> {
        override val exactName = "PositiveInt"

        override fun Raise<String>.spec(raw: Int): PositiveInt {
            ensure(raw > 0) { "$raw is not > 0" }
            ensure(raw.isFinite()) { "Is not a finite number" }
            return PositiveInt(raw)
        }
    }
}

PositiveInt.from(42)
// Either.Right(PositiveInt(42))
PositiveInt.from(-5)
// Either.Left("-5 is not > 0")
```

The revised data model takes more code, but it gives you one important property:

> If any of your functions accepts an instance of `order: Order`, you immediately know that it's a valid order and no validation logic is required.

This is **validation by construction** and it eliminates undesirable cases asap which greatly simplifies your domain logic. By making our data model explicit we fixed:

- Order `count` of zero, negative, or infinity by explicitly requiring a `PositiveInt` (unfortunately, that happens at runtime because the compiler can't know if a given integer is positive or not by just looking at the code).
- The `UUID`s now can't be messed up because the compiler will give you an error, if for example you try to pass `UserId` to a function accepting `OrderId`.
- The `time` is now always in UTC by using `Instant`.
- The `trackingId` is trimmed and can't be blank.

To learn more about Exact types you can check [the Arrow Exact GitHub repo](https://github.com/arrow-kt/arrow-exact). The benefit of explicit data models is correctness and reduced complexity of your core logic.

> Not all types should be exact. For example, we make an exception for DTOs and entities where working with primitives is easier.
> However, we still use ADTs and everything in the domain layer where the business logic must be exact and explicit.
