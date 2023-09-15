# Ivy's Architecture

Ivy Wallet's architecture is simple - use the shortest solution possible that works and doesn't break the app. We don't follow strict rules, patterns or paradigms, we just do whatever makes the most sense.

Although here a few **principles that we like:**

- **80/20:** 20% of the code brings 80% of the user value
- **Don't walk away from complexity, run!** If you can't explain it to a 5 yr old, delete it and start over.
- **Don't overengineer.** Less is more. The best developers come up with the "dumbest" solutions. Be pragmactic.
- **Build for today cuz tomorrow may never come.** Building for the future is a sure way to fail in the present.

Enough philosophy, just be yourself. Keep it simple and let's dive into the details.

## Screen: Compose UI + ComposeViewModel

We separate the Compose UI from the screen logic using a ViewModel. We do that because of the following benefits:
1. **Jetpack Compose perfomance:** The less computations the Compose UI does, the faster it'll recompose. Also, Compose will recompose fewer times if its params are primitives or **@Immutable** data.
2. **DI (Dependency Injection):** You can't inject dependencies using Hilt/Dagger/Anvil in a composable function but you can easily do so via `@Inject constructor` in the ViewModel.
3. **Simpler code:** "dumb" UI that only draws a primitive UiState and pure business logic without UI/Android dependencies.
4. **Bonus: Compose Previews:** When the `UiState` is a simple data class you can mock any arbitrary preview for all possible states. Otherwise, good luck previewing HTTP calls, etc.

![screen-viewmodel](../assets/screen-vm.svg)

**How it works? TL;DR;**
- **Sceen (UI):** very dumb. Displays a snapshot of a **UiState** and sends user interaction as **UiEvent**s to the **ViewModel**.
- **UiState:** usually a `data class` with a bunch of primitive `val`s that are displayed in the UI. _Note: The `UiState` must be optimized for Compose and contain only **@Immutable** structures so Compose can recompose efficiently._
- **UiEvent:** a snapshot of a user interaction _(e.g. button click, entered text change, checkbox checked change, etc)_
- **ViewModel:** produces the current `UiState` and handles all `UiEvents`. Encapsulates the screen's logic and does the CRUD/IO operations.

**Compose in the ViewModel - Why?**

The reason is very pragmactic - the Compose runtime API (e.g. Compose state, `remember`, effects) is simpler and more powerful:

- No need to use complex `combine` and `flattenLatest` Kotlin Flow chains.
- You have access to `LaunchedEffect`, `remember` and the entire Compose runtime.
- The above leads to more simple and less nested code.

> Tip: In Ivy Wallet, inherit the `ComposeViewModel` base class and you're good to go!

## Modularization: by screen/feature

We split our app into multiple modules to reduce coupling (spaghetti code) and make the it build faster. Also, this allows multiple contributors to work on different features without merge conflicts.

Another big benefit is that each screen can have a simple package structure and be able to scale with more code to support complex use cases w/o affecting the rest of the code.

![modularization-strategy](../assets/modularization.svg)

Our modularization strategy is simply:
- We have a few shared `:ivy-core`, `:ivy-design`, `:ivy-navigation` and `:ivy-resources`.
- Use the above modules to access the shared code in your screens.
- Each screen is in a separate `:screen-home`, `:screen-something` module.

To create a new module just run:
```
./scripts/create_module.sh screen-something
```

> Simplification: We have a few modules that are an exception to this strategy, for example widgets `:widget-something` and other things worthy of encapsulation in an own module. But use the above strategy as a rule-of-thumb.


## Paradigm: pragmatic

Ivy Wallet is a multi-paradigm project. Follow whatever paradigm that you prefer. We appreciate both OOP (Object Oriented Programming) and FP (Functional Programming), use the best from both worlds.

**Just avoid:**
- Inheritance
- Complex design patterns

That being said, we lean more towards the FP world because of its simplicity and safety. Ivy Wallet is built on top [ArrowKt](https://arrow-kt.io/) so take advantage of it. 

> Tip: Arrow's **Either<Left, Right>** is very useful for modeling operations that may result in either success (right) or error (left), for example HTTP requests.

## Clean code: NO 🚫

I added it for a bit of controversy but IMO the term "Clean" has lost meaning and it often leads to unnecessary layers of abstractions => more complexity, lots of boilerplate and worse performance. Keep it simple, be like **[Grug](https://grugbrain.dev/)**.

> Tip: Read the [The Grug Brained Developer article](https://grugbrain.dev/) it's fun and have some wisdom in it.
