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

- What to show if `loading = false`, `content == null`, `error == null`?
- What to do if we have both `loading = true` and `error != null`?

There some many ways things to go wrong - for example, a common one is forgetting to reset `loading` back to `false`.
A better way to model this would be to use [Algebraic Data types (ADTs)](https://wiki.haskell.org/Algebraic_data_type) 
or simply said in Kotlin: `data classes`, `sealed interfaces`, and combinations of both.

```kotlin
sealed interface ScreenUiState {
  data object Loading : ScreenUiState
  data class Content(val text: String) : ScreenUiState
  data class Error(val msg: String) : ScreenUiState
}
```

With the ADTs representation, we eliminate the impossible cases of having a `content` and `error` at the same time 
or `loading = false` and nulls for both `content` and `error`. We also eliminate that on compile-time, meaning that
whatever shit will do - the compiler will never allow this code to run.

**Takeaway:** Model your data using `data classes`, and  `sealed interfaces` (and combinations of them) in a way that:

- Mirrors your domain one-to-one, exactly and explicitly.
- Impossible cases are eliminated by construction and on compile time.

## Explicit Data Types
