# Ivy's Ways, These Are

Solutions short and working, must be. Break the app, they must not.

**Wisdom we follow:**

- **80/20 rule:** From 20% code, 80% value.
- **Complexity, a dark path it is.** Easy to start, difficult to undo. More layers, tempting they seem... Yet in simplicity, true power lies.
- **Overengineer, you must not.** More, often is less. Clear and plain, solutions should stand.
- **For today, build.** Tomorrow, uncertain it is. By looking too far ahead, stumble today, you could.

## Screen: Compose UI + ComposeViewModel

Separate, the path of wisdom it is: **Compose UI** <> **Logic of Screen**. Why? Hmmm...

1. **Performance of Jetpack Compose:** Less the UI thinks, faster it changes. Fewer changes when `UiState` primitive and `@Immutable`.
2. **DI (The Way of Injecting):** Inject in composable functions, one cannot. Yet, in ViewModel with `@Inject constructor`, easy it is.
3. **Code, simple it is:** UI dumb, only draws. Logic pure, without ties to the Android realm.
4. **Compose Previews:** Mock `UiState`, easy it becomes. Any screen state, preview you can. 

![screen-viewmodel](../assets/screen-vm.svg)

### Quick understanding, you seek?

- **Screen (UI):** Dumb, it is. Only draws `UiState`. When clicked sends `UiEvent`s to ViewModel. Uses UI components from Material3 and Ivy Design.
- **UiState:** A `data class`, often it is. Holds only primitve and `@Immutable` values it should. Dumb Compose UI, plays well only with primitives.
- **UiEvent:** User's doings, captured they are _(like button presses, text changes, checkbox states)._
- **ViewModel:** To the UI, `UiState` it offers. Ears open to `UiEvents`, it keeps. Logic of screen it holds, and tasks it performs. Compose runtime ViewModel's force is.

### In ViewModel, why Compose you ask?

Reason, straightforward it is. More strength and simplicity, the Compose runtime has:

- Complex Kotlin Flow chains and `combine`, no need there is.
- Power of `LaunchedEffect`, `remember`, and all Compose gifts, you have.
- Simpler and less tangled code, this gives.

> **A tip for you:** In Ivy Wallet, the `ComposeViewModel` base inherit, and set you are.

## Architecture Overview

Follow [offical Guide to app architecture by Google](https://developer.android.com/topic/architecture), we do. Not for fleeting trends or mere style, but for wisdom it holds. Simplicity, at its core.

![app-architecture](../assets/app-layers.svg)

### [Data Layer](https://developer.android.com/topic/architecture/data-layer)

Foundation it is, where data originates and resides. Persistence with databases, or fetch from afar through network calls, it handles. Stable and reliable, it must be, for all above to trust.

### [Domain Layer](https://developer.android.com/topic/architecture/domain-layer)

Heart of the business logic, here it beats. Knowledge of UI or data sources, it has **not**. Pure and free from Android concerns, it remains. Transforms data into meaningful actions, and sets the rules the app lives by.

### [UI Layer](https://developer.android.com/topic/architecture/ui-layer)

Face of the app, this is. Interactions with users, here they unfold. Displays data and listens to the user, it does. Lean it is, relying on lower layers for knowledge and truth. Its beauty, not just skin-deep, but in its simplicity and responsiveness.

## Modularization: by screen/feature

Split our app into many modules, we do. Reduce tangled paths (spaghetti code) and faster app builds, it aims. Furthermore, permits many hands to craft different features, free from merging conflicts.

Each screen, simple it stands. With more code it grows, supporting intricate tales. Yet, disturb the rest, it does not. In harmony, the entire codebase remains.

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

> Only `implementation("...")` dependencies are allowed. Usage of `api("...")` is banned for the sake of simplicity and performance. Motivation: No `api` usage 🚫 => no tricky Gradle problems to solve.

### Shared code modules

These are the modules that you can use as a dependency in your screen/feature module:

- `:ivy-base`: code that needs to be shared everywhere.
- `:ivy-resources`: contains all Ivy Wallet's resources - **strings** and **drawables**.
- `:ivy-design`: Ivy's design system (colors, typography and shapes) and provides a stylized Material3 (M3) theme.
- `:ivy-data`: [Data layer](https://developer.android.com/topic/architecture/data-layer). Encapsulates CRUD operations. Holds `DataSources` (Room DB, Datastore, Networking) and `Repositories` (validation and mapping logic).
- `:ivy-domain`: [Domain layer](https://developer.android.com/topic/architecture/domain-layer). Contains Ivy Wallet's business logic.
- `:ivy-navigation`: provides the definition _(screen's startup params w/o implementation)_ of all Screen destinations in Ivy Wallet and implements a simple back-stack based custom `Navigation`. 
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
