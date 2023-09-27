# Ivy's Architecture

Ivy Wallet's architecture is simple - use the shortest solution possible that works and doesn't break the app. We don't follow strict rules, patterns or paradigms. Just do whatever makes the most sense.

Although, here are a few **principles that we like:**

- **80/20:** 20% of the code brings 80% of the user value
- **Don't walk away from complexity, run!** If you can't explain it to a 5-year-old, delete it and start over.
- **Don't overengineer.** Less is more. The best developers come up with the "dumbest" solutions. Be pragmatic.
- **Build for today cuz tomorrow may never come.** Building for the future is the best way to fail in the present.

Enough philosophy, just be yourself. Keep it simple and let's dive into the details.

## Screen: Compose UI + ComposeViewModel

We separate the Compose UI from the screen logic using a ViewModel. We do that because of the following benefits:
1. **Jetpack Compose performance:** The fewer computations the Compose UI does, the faster it'll recompose. Also, Compose will recompose fewer times if its params are primitives or **@Immutable** data.
2. **DI (Dependency Injection):** You can't inject dependencies using Hilt/Dagger/Anvil in a composable function but you can easily do so via `@Inject constructor` in the ViewModel.
3. **Simpler code:** "dumb" UI that only draws a primitive UiState and pure business logic without UI/Android dependencies.
4. **Bonus: Compose Previews:** When the `UiState` is a simple data class you can mock any arbitrary preview for all possible states. Otherwise, good luck previewing HTTP calls, etc.

![screen-viewmodel](../assets/screen-vm.svg)

**How it works? TL;DR;**
- **Sceen (UI):** very dumb. Displays a snapshot of a **UiState** and sends user interaction as **UiEvent**s to the **ViewModel**. Uses Material3 components and reusable Ivy ones.
- **UiState:** usually a `data class` with a bunch of primitive `val`s that are displayed in the UI. _Note: The `UiState` must be optimized for Compose and must contain only primitives and **@Immutable** structures so Compose can recompose efficiently._
- **UiEvent:** a snapshot of a user interaction _(e.g. button click, entered text change, checkbox checked change, etc)_
- **ViewModel:** produces the current `UiState` and handles all `UiEvents`. Encapsulates the screen's logic and does the CRUD/IO operations.

**Compose in the ViewModel - Why?**

The reason is very pragmatic - the Compose runtime API (e.g. Compose state, `remember`, effects) is simpler and more powerful:

- No need to use complex `combine` and `flattenLatest` Kotlin Flow chains.
- You have access to `LaunchedEffect`, `remember` and the entire Compose runtime.
- The above leads to more simple and less nested code.

> Tip: In Ivy Wallet, inherit the `ComposeViewModel` base class and you're good to go.

## Modularization: by screen/feature

We split our app into multiple modules to reduce coupling (spaghetti code) and make the app build faster. Also, this allows multiple contributors to work on different features without merge conflicts.

Another big benefit is that each screen can have a simple package structure and be able to scale with more code to support complex use cases without affecting the rest of the codebase.

![modularization-strategy](../assets/modularization.svg)

Our modularization strategy is simple:
- We have a few shared `:ivy-*` modules.
- Use the above modules to access the shared code in your screens.
- Each screen is in a separate `:screen-home`, `:screen-something` module.

> Simplification: We have a few modules that are an exception to this strategy (for example widgets like `:widget-balance` and other shared modules) but the above strategy holds true in most cases.


### Creating a new module

To create a new module just run:
```
./scripts/create_module.sh screen-something
```

> If you're creating a new screen you'll need to add its input params (definition) to `Screens.kt` (:ivy-navigation) and wire its implementation in `IvyNavGraph.kt` (:app).

### Modularization gotchas

- Screens (`:screen-something`) **cannot** depend on other screens. They can only depend on `:ivy-*` shared code modules.
- If you need to re-use code between screens, you need to move it to a shared code module like `:ivy-domain` or `ivy-domain-ui`.
- The `:ivy-navigation` does **not** contain screen's implementation but only the screen's definition. A screen definition is a `data class` / `data object` class that models the screen's startup params. This way different screens can start other screen without knowing about their implementation.
- Only `:app` knows about the implementation of all screens. It maps each `Screen` definition from `:ivy-navigation` to the actual implementation in `IvyNavGraph.kt` (in `:app`).

> Only `implementation("...")` dependencies are allowed. Usage of `api("...")` is banned for the sake of simplicity and performance. No `api("...")` => no tricky Gradle problems to solve.

### Shared code modules

These are the modules that you're allowed to use as a dependency in your own modules:

- `:ivy-base`: very light-weight low-level shared code that needs to be shared almost everywhere.
- `:ivy-resources`: contains all Ivy Wallet's resources (e.g. strings, drawables). It's used to share string and drawable resources between different modules. _This resources monolithic approach makes it easier translating the app  (only one `strings.xml`) and protects us from duplicated resources. Also, it's simple._
- `:ivy-design`: defines the Ivy design system (colors, typography and shapes) and provides a stylized Material3 (M3) theme. It also adds re-usable Ivy components that are missing in M3. _At some point may be extracted as a standalone open-source library._
- `:ivy-persistence`: Encapsulated CRUD logic - currently Room DB and Datastore.
- `:ivy-domain`: contains Ivy Wallet's data model, business logic and CRUD operations (Datastore, Room DB). Use it to interact or extend Ivy Wallet's domain.
- `:ivy-navigation`: provides the definition (screen's startup params w/o implementation) of all Screen destinations in Ivy Wallet and implements a simple back-stack based custom `Navigation`. 
- `:ivy-domain-ui`: Builds re-usable high-level UI components (for example `AccountModal` that also encapsulates the account CRUD logic) that enforce consistent Ivy UI/UX patterns for common operations (e.g. CRUD for accounts, categories, etc).

> ⚠️ WARNING ⚠️: The above shared modules are under construction. We're also actively trying to get rid of legacy code that we partly encapsulated in the `:temp-legacy-code` and `:temp-old-design` modules.

## Building Compose UI

For majority of screens we don't have a Figma design. That's why you gotta get resourceful. 

In a nutshell, we bet on [Material3 (Material You) Design](https://m3.material.io/) and follow the UI/UX best practices defined by Google.

Here are a few principles to help you make a better Compose UI:

- **Material3 components:** Use Material3 (M3) components with as little customzation as possible. _The M3 designers and creators are knowedgable people and they likely did better job than us._
- **Ivy UI components:** We also got some re-usable UI components of our own that live in `:ivy-design` and `:ivy-domain-ui`. Add those modules as a dependency and take advantage of them.
- **Deprecation ban:** Do **not** use deprecated components.

In terms of code quality, just follow and address the feedback provided from our CI checks powered by [Detekt](https://detekt.dev/) and [Slack's compose-lints](https://slackhq.github.io/compose-lints/).

> 💡 The less you customize your UI components, the shorter the code. Also, this makes it easier for us to apply global styling and make the UI more consistent and more customizable to user's prefered theme, colors, fonts and shapes.

## Paradigm: pragmatic

Ivy Wallet is a multi-paradigm project. Follow whatever paradigm that you prefer. We appreciate both OOP (Object Oriented Programming) and FP (Functional Programming) and use the best of both worlds.

**Just avoid:**
- Inheritance
- Complex design patterns

That being said, we **lean more towards the FP world** because of its simplicity and safety. Ivy Wallet is built on top of [ArrowKt](https://arrow-kt.io/) so take advantage of it. 

> Tip: Arrow's **Either<Left, Right>** is very useful for modeling operations that may result in either success (right) or error (left), for example, HTTP requests.

## Clean code: NO 🚫

I added it for a bit of controversy but IMO the term "Clean" has lost meaning and it often leads to unnecessary layers of abstractions => more complexity, lots of boilerplate, and worse performance. Keep it simple, be like **[Grug](https://grugbrain.dev/)**.

> Tip: Read the ["The Grug Brained Developer"](https://grugbrain.dev/) article, it's fun and has some good wisdom in it.

## Legacy code: we're full of it!

Ivy Wallet is a hobby project from 2018. You can imagine that during the last 5 years, it accumulated a lot of legacy code. 😬 

While this sucks... It's also a good opportunity to travel through time and see how the Android architecture evolved over the years. If you like modern technologies and want to help us out, feel free to **[check our "devexp" GitHub issues](https://github.com/Ivy-Apps/ivy-wallet/labels/devexp)** _(devexp meaning Developer's Experience)_ and to try to fix one.

[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)

> We need help migrating legacy code screens to `ComposeViewModel` and the MVI/MVVM pattern that you've just read in this doc.

## Questions or feedback?

Please, reach us on the "Development" topic of our private Telegram community.

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)
