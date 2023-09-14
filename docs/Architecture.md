# Ivy's Architecture

Ivy Wallet's architecture is simple - use the shortest solution that works and doesn't break the app. We are no fanatics who follow strict rules, patterns or paradigms. We simply do whatever makes sense.

Although there a few **principles that we like:**

- **80/20:** 20% of the code brings 80% of the user value
- **Don't walk away from complexity, run!** If you can't explain it to a 5 yr old, delete it and start over from scratch.
- **Don't overengineer.** Less is more. The best developers come up with the "dumbest" solutions. Be pragmactic.
- **Build for today cuz tomorrow may never come.** Building for the future is a sure way to fail in the present.

Enough philosophy, just be yourself - keep it simple and let's dive into the details.

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
- The above leads to a simple, straightforward and not nested code.

## Modularization: by screen/feature

We split the app into multiple modules to reduce coupling (spaghetti code) and make the all build faster. Also, this allows multiple contributors to work on different features without merge conflicts.

⚠️ WIP: Modularization diagram ⚠️

Our modularization strategy is simply:
- We have a few shared `:ivy-core`, `:ivy-design`, `:ivy-navigation` and `:ivy-resources`.
- Use the above modules to access the shared code in your screens.
- Each screen is in a separate `:screen-home`, `:screen-something` module.

## Paradigm: pragmatic

Ivy Wallet is a multi-paradigm project. Follow whatever paradigm that you prefer. We appreciate both OOP (Object Oriented Programming) and FP (Functional Programming), use the best from both worlds.

**Just avoid:**
- Inheritance
- Complex design patterns

That being said, we lean more towards the FP world because of its simplicity and safety. Ivy Wallet is built on top [ArrowKt](https://arrow-kt.io/) so take advantage of it. 

> Tip: Arrow's **Either<Left, Right>** is very useful for modeling operations that may result in either success (right) or error (left), for example HTTP requests.

## Clean code: NO 🚫

I added it for a bit of controversy but IMO the term "Clean" has lost meaning and it leads to unnecessary layers of abstractions => more complexity, lots of boilerplate and worse performance. Keep it simple, be like [Grug](https://grugbrain.dev/).
