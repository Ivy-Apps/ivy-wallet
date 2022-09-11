# Architecture Decision Record (ADR) #2: Going Reactive ✅

1) Replace all operations in the domain's logic with Kotlin's `Flow` asynchronous data-stream API so they'll automatically react to data changes by emitting new values.

2) Leverage reactive data sources:
- Room DB Flow DAOs
- DataStore Flow

3) Encapsulate use-cases in [FlowAction]([core/actions/](https://github.com/Ivy-Apps/ivy-wallet/blob/develop/core/actions/src/main/java/com/ivy/core/action/FlowAction.kt)).

## Problem

Ivy Wallet UI should update every time [transaction, account, category, exchange rate, base currency, ...] change. Doing this imperatively inceases complexity and make ViewModels gigantous!

## Solution

Go reactive by migrating to [Kotlin Flows](https://kotlinlang.org/docs/flow.html). See [Flow Actions](https://github.com/Ivy-Apps/ivy-wallet/blob/develop/core/actions/src/main/java/com/ivy/core/action).

## Benefits

- The app will react automatically to data changes.
- Flows can introduce out-of-the-box caching and efficient async processing.
- Reducing complexity by not thinking which states we need to update imperatively (manually).