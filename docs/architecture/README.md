# Architecture

[Ivy Wallet](https://play.google.com/store/apps/details?id=com.ivy.wallet) follows the functional programming paradigm and its architecture is influenced by that fact.

![architecture.svg](../../assets/architecture.svg)
**[--> View the diagram full-screen <--](https://raw.githubusercontent.com/Ivy-Apps/ivy-wallet/develop/assets/architecture.svg)**

### Principles

- Push side-effects (IO, impure code) to the edges.
- Prefer **Data** over **Calculations**.
- Prefer **Calculations** over **Actions**.
- Don't set values, **react (RX)** to changes instead.

## Concepts

### Data

[ADTs (Algebraic Data Types)](https://en.wikipedia.org/wiki/Algebraic_data_type#) describing the domain as strictly as possible. In a nutshell ADTs are:
- Product types: `&&` AND _(`*` multiplication in maths)_
```kotlin
// Product (AND) type because
// all 3 values are required
data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int
)
```

- Sum types: `||` OR _(`+` sum in maths)_
```kotlin
// AppTheme can be either Light, Dark or Auto
enum class AppTheme { Light, Dark, Auto}

// It's either Option.A containing an Int
// or Option.B which has params
sealed interface Option {
    data class A(val a: Int): Option

    object B : Option
}
```

The idea is to define the domain using Sum (`||`) and Product (`&&`) types by combining them and constraining the types to construct only valid values.

To see ADTs in action visit [`:core:data-model`](../../core/data-model).

### Calculations

Calculations are "pure" _(also known as referential transparent)_ fuctions that have no side effects. Simply said:
- When called with the same arguments, they always return the same result.
- Don't change the outside world (e.g. write to database, send data to server).
- Don't read data from the outside world (e.g. get current device time).
- Calculations don't mutate state and must not throw exceptions.

### Actions

Functions that have side-effects, the ones Android Devs are the most familiar with. For example, read/write to the dabase, send HTTP requests and so on.

This code is usually hard to test and sometimes unpredictible. Avoid it.

## UI (screens) architecture

We follow a combination of the MVVM and MVI architectural patterns optimized to work well with [Functional Reactive Programming](https://www.toptal.com/android/functional-reactive-programming-part-1).

- Single @Immutable `UIState` made of Compose primitives.
- Single `Event` sum type with all possible user interactions.
- [FlowViewModel](../../core/domain/src/main/java/com/ivy/core/domain/FlowViewModel.kt) producing a `Flow` of UI states and receiving a `Flow` events.
- @Composable UI functions that transform the latest `UIState` to UI and emit user interactions as `Event`.

## ADRs

Important and pivotal tech decisons documented as **ADRs (Architecture Decision Records)** including a context to the problem, **trade-off analysis** and a potential solution.