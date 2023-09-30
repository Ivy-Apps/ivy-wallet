# Ivy's Ways, These Are

## ⚠️ UNDER CONSTRUCTION ⚠️

Solutions short and working, must be. Break the app, they must not.

**Wisdom we follow:**

- **80/20 rule:** From 20% code, 80% value.
- **Complexity, a dark path it is.** Easy to start, difficult to undo. More layers, tempting they seem... Yet in simplicity, true power lies.
- **Overengineer, you must not.** More... often is less. Clear and plain, solutions should stand.
- **For today, you build.** Tomorrow, uncertain is. By looking too far ahead, stumble today, you might.

## Screen: Compose UI + ComposeViewModel

Separation, the path of wisdom it is: **Compose UI** <> from **Logic of Screen**. Why? Hmmm...

1. **Performance of Jetpack Compose:** Less the UI thinks (computes), faster it changes (recomposes). Fewer changes when `UiState` is primitive and `@Immutable`.
2. **DI (The Way of Injecting):** Inject in composable functions, one cannot. Yet, in ViewModel with `@Inject constructor`, easy it is.
3. **Code, simple it is:** UI dumb, only draws. Logic pure, without knowing about UI doings.
4. **Compose Previews:** Mock `UiState`, easy it becomes. Any screen state, preview you can. 

![screen-viewmodel](../assets/screen-vm.svg)

### Quick understanding, you seek?

- **Screen (UI):** Dumb, it is. Only draws `UiState`. When clicked sends `UiEvent`s to ViewModel. Uses UI components from Material3 and Ivy Design.
- **UiState:** A `data class`, often it is. Holds only primitve and `@Immutable` values it should. Dumb Compose UI, plays well only with primitives.
- **UiEvent:** User's doings, captured they are _(like button presses, text changes)._
- **ViewModel:** To the UI, `UiState` it offers. Ears open to `UiEvents`, it keeps. Logic of screen it holds, and tasks it performs. Compose runtime ViewModel's force is.

### In ViewModel, why Compose you ask?

Reason, straightforward it is. More strength and simplicity, the Compose runtime has:

- Complex Kotlin Flow chains and  `combine`, no need there is.
- Power of `LaunchedEffect`, `remember`, and all Compose gifts, you have.
- Simpler and less tangled code, this gives.

> **A tip for you:** In Ivy Wallet, the `ComposeViewModel` base inherit, and set you are.

## Architecture Overview

Follow [offical Guide to app architecture by Google](https://developer.android.com/topic/architecture), we do. Not for fleeting trends or mere style, but for wisdom it holds. Simplicity, at its core.

![app-architecture](../assets/app-layers.svg)

### [Data Layer](https://developer.android.com/topic/architecture/data-layer)

Foundation it is, where data originates and resides. Persistence with databases, or fetch from afar through network calls, it handles. Stable and reliable, it must be, for all above to trust. 

Two they are. `DataSource`: wrapped source of raw information like a Room DB, DataStore or a Network API, you see. And `Repository` data sources force user, for good CRUD to do.

### [Domain Layer](https://developer.android.com/topic/architecture/domain-layer)

Heart of the business logic, here it beats. Knowledge of UI or data sources, it has **not**. Pure and free from Android and persistence concerns, it remains. Transforms data into meaningful actions, and sets the rules the app lives by. 

Ivy Wallet's balance computations here they are done. `Repository` it can only wield. The `DataSource` forbidden it is.

### [UI Layer](https://developer.android.com/topic/architecture/ui-layer)

Face of the app, this is. Interactions with users, here they unfold. Displays data and listens to the user, it does. Lean it is, relying on lower layers for knowledge and truth. Its beauty, not in its brain, but in its looks and responsiveness. 

Screens here they live, and they follow their path. Always two, no more, no less - Compose UI and `ComposeViewModel` they are.

## Modularization: by screen/feature

Split our app into many modules, we do. Reduce tangled paths (spaghetti code) and faster app builds, it aims. Furthermore, permits many hands to craft different features, free from merging conflicts.

Each screen, simple it stands. With more code it grows, supporting intricate tales. Yet, disturb the rest, it does not. In harmony, the entire codebase remains.

![modularization-strategy](../assets/modularization.svg)

**Simple, our modularization path is:**

- Shared `:ivy-*` modules, a few we possess.
- With these modules, shared wisdom in your screens you access.
- Distinct, each screen/feature stands, like `:screen-home`, `:feature-something` they are.

> Know this, young coder: Exceptions, a few modules have (widgets such as `:widget-balance`, for instance). Yet, the truth in the above strategy, mostly it remains.

### Creating a new module

To Birth a New Module... Invoke this chant in your terminal, you must:
```
./scripts/create_module.sh screen-something
```

> If crafting a screen, you must also inscribe its essence in `Screens.kt` (:ivy-navigation) and weave its spirit in `IvyNavGraph.kt` (:app).

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
