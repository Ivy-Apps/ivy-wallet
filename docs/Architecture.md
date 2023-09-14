# Ivy's Architecture

Ivy Wallet's architecture is simple - use the shortest solution that works and doesn't break the app. We are no fanatics/sectarians that follow strict rules/patterns/paradigms - we do whatever makes sense.

Although there a few principles that we like:

- **80/20:** 20% of the code brings 80% of the user value
- **Don't walk away from complexity, run!** If you can't explain it to a 5 yr old, delete it and start from scratch.
- **Don't overengineer.** Less is more. The best developers come up with the "dumbest" solutions. Be pragmactic.
- **Build for today cuz tomorrow may never come.** Building for the future is a sure way to fail in the present.

Enough philosophy, just be yourself - keep it simple and let's dive to the details.

## Screen: Compose UI + ComposeViewModel

We separate the Compose UI from the screen logic using a ViewModel. We do that because of the following benefits:
1. **Jetpack Compose perfomance:** The less computations the UI does, the faster it'll recompose. Also, Compose will recompose fewer times if it only works with primitives and **@Immutable** data.
2. **DI (Dependency Injection):** You can't inject dependencies using Hilt in a composable function but you can easily do so via `@Inject constructor` in the ViewModel.
3. **Simpler code:** results in dumb UI and pure business logic without the UI complexities.
4. **Bonus: Compose Previews:** When the `UiState` is a simple data class you can mock any arbitrary previews for all possible states. Otherwise, good luck previewing HTTP calls, etc.

![Screen-Viewmodel](../assets/screen-vm.svg)

**How it works? TL;DR;**
- **Sceen (UI):** very dumb. Displays a snapshot of a **UiState** and sends user interaction events as **UiEven** to the **ViewModel**.
- **UiState:** usually a `data class` with a bunch of primitive `val`s that are displayed in the UI. _Note: The `UiState` must be optimized for Compose and contain only **@Immutable** structures so Compose can run efficiently._
- **UiEvent:** defines all possible user interections _(e.g. user clicks a button or enters a text, or checks a checkbox or slides a slider)_
- **ViewModel:** produces the current `UiState` and handles `UiEvents`. Encapsulates the screen's logic and does the CRUD/IO operations.

**Compose in the ViewModel - Why?**

The reason is very pragmactic - the Compose runtime (using Compose states) API is simpler and more powerful:

- No need to use complex `combine` and `flattenLatest` Flow APIs.
- You have access to `LaunchedEffect`, `remember` and the entire Compose runtime.
- The above results in a simple and not nested code.

## Modularization: by screen/feature

⚠️ WIP...

## Paradigm: pragmatic

⚠️ WIP...

## Clean code: NO 🚫

⚠️ WIP...
