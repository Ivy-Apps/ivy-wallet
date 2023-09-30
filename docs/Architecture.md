# Ivy's Ways, These Are

Solutions short and working, must be. Break the app, they must not.

**Wisdom we follow:**

- **80/20 rule:** From 20% code, 80% value.
- **Complexity, a dark path it is.** Easy to start, difficult to undo. More layers, tempting they seem... Yet in simplicity, true power lies.
- **Overengineer, you must not.** More... often is less. Clear and plain, solutions should stand.
- **For today, you build.** Tomorrow, uncertain is. By looking too far ahead, stumble today, you might.

## Screen: Compose UI + ComposeViewModel

Separation, the path of wisdom it is: **Compose UI** <> **Logic of Screen**. Why? Hmmm...

1. **Performance of Jetpack Compose:** Less the UI thinks (computes), faster it changes (recomposes). Fewer recompositions when `UiState` is primitive and `@Immutable`, app smoother runs.
2. **DI (The Way of Injecting):** Inject in composable functions, one cannot. Yet, in ViewModel with `@Inject constructor`, easy it is.
3. **Code, simple it is:** UI dumb, only draws. Logic pure, without knowing about UI doings.
4. **Compose Previews:** Mock `UiState`, easy it becomes. Any screen state, preview you can. 

![screen-viewmodel](../assets/screen-vm.svg)

### Quick understanding, you seek?

- **Screen (UI):** Dumb, it is. Only draws `UiState`, thinks not. When clicked sends `UiEvent`s to ViewModel. Uses UI components from Material3 and Ivy Design.
- **UiState:** A `data class`, often it is. Holds only primitve and `@Immutable` values it should.
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

Always two they are. `DataSource` a wrapped source of raw information it is, tapping in Room DB, DataStore or a Network API forces it does. And `Repository` data sources force it wields, for good CRUD and validation to do.

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

To birth a new module... Invoke this chant in your terminal, you must:
```
./scripts/create_module.sh screen-something
```

> If crafting a screen, you must also inscribe its essence in `Screens.kt` (:ivy-navigation) and weave its spirit in `IvyNavGraph.kt` (:app).

## Other wisdoms

["The Grug Brained Developer"](https://grugbrain.dev/) article, read you should. Ancient engineering wisdom and entertainment, there loves.

Legacy code, we have - yes. Check the ["devexp" issues](https://github.com/Ivy-Apps/ivy-wallet/labels/devexp) to bring balance in the code force, you should.

## Uncertain, you are?

In the "Development" topic of our [Telegram community](https://t.me/+ETavgioAvWg4NThk), Ivy wisdom you should seek.

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)
[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)
