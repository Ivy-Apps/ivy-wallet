# Ivy's Ways, These Are

[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)
[![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers)
[![Fork Ivy Wallet](https://img.shields.io/github/forks/Ivy-Apps/ivy-wallet?logo=github&style=social)](https://github.com/Ivy-Apps/ivy-wallet/fork)

Solutions short and working, must be. Break the app, they must not.

**Wisdom we follow:**

- **80/20 rule:** From 20% code, 80% value.
- **Complexity, a dark path it is.** Easy to start, difficult to undo. More layers, tempting they seem... Yet in simplicity, true power lies.
- **Overengineer, you must not.** More... often is less. Clear and plain, solutions should stand.
- **For today, you build.** Tomorrow, uncertain is. By looking too far ahead, stumble today, you might.

## Screen: Compose UI + ComposeViewModel

Separation, the path of wisdom it is: **Compose UI ≠ Logic of Screen**. Why? Hmmm...

1. **Performance of Jetpack Compose:** Less the UI thinks (computes), faster it changes (recomposes). Fewer recompositions when `UiState` is primitive and `@Immutable`, app smoother runs.
2. **DI (The Way of Injecting):** Inject in composable functions, one cannot. Yet, in ViewModel with `@Inject constructor`, easy it is.
3. **Code, simple it is:** UI dumb, only draws. Logic pure, not knowing about UI doings.
4. **Compose Previews:** Mock `UiState`, easy it becomes. Any screen state, preview you can.
5. **Testability:** ViewModel, easy to test it is - send `UiEvent`s, verify `UiState`. Simple. Hmmm... Compose UI, like celebrity is. Mocked `UiState` if you pass, [Paparazzi](https://github.com/cashapp/paparazzi) screenshot tests pass will.

![screen-viewmodel](../assets/screen-vm.svg)

### Quick understanding, you seek?

- **Compose UI:** Dumb, it is. Only draws `UiState`, thinks not. `UiEvent`s it screams when touched.
- **UiState:** A `data class`, often it is. Holds only primitive and `@Immutable` values it should.
- **UiEvent:** User's doings, captured they are _(like button presses, text changes)._
- **ViewModel:** To the UI, `UiState` it offers. Ears open to `UiEvents`, it keeps. Logic of screen it holds, and tasks it performs. Ancient Compose runtime's force it wields.

> [Unidirectional Data Flow (UDF)](https://developer.android.com/jetpack/compose/architecture#udf) wisdom this teaches. [MVI (Model View Intent)](https://staltz.com/unidirectional-user-interface-architectures.html) it is called.

### In ViewModel, why Compose you ask?

Reason, straightforward it is. More strength and simplicity, the Compose runtime has:

- Complex Flow chains and  `combine`, **no need** there is.
- Power of `LaunchedEffect`, `remember`, and all Compose gifts, you have.
- Simpler and less tangled code, this gives.

> **A tip for you:** In Ivy Wallet, the `ComposeViewModel` base inherit, and set you are.

## Architecture Overview

Follow [offical Guide to app architecture by Google](https://developer.android.com/topic/architecture), we do. Not for fleeting trends or mere style, but for wisdom it holds. Simplicity, at its core.

![app-architecture](../assets/app-layers.svg)

### [Data Layer](https://developer.android.com/topic/architecture/data-layer)

Foundation it is, where data originates and resides. Persistence with databases, or fetch from afar through network calls, it handles. Stable and reliable, it must be, for all above to trust. 

In the Data Layer, a balance like the sith, there is. Always two there are, neither more nor less. `DataSource`, the bearer of raw data, using the might of [Room DB](https://developer.android.com/training/data-storage/room), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore), or Network API. Then `Repository`, wielding the power of data sources, for rightful **CRUD and validation**, it strives.

### [Domain Layer](https://developer.android.com/topic/architecture/domain-layer)

Heart of the business logic, here it beats. Knowledge of UI or `DataSource`s, it has **not**. Only power of `Repository` it wields. Pure and free from Android and persistence concerns, it remains. Transforms data into meaningful actions, and sets the rules the app lives by. 

### [UI Layer](https://developer.android.com/topic/architecture/ui-layer)

Face of the app, this is. Interactions with users, here they unfold. Displays data and listens to the user, it does. Lean it is, relying on lower layers for knowledge and truth. Its beauty, not in its brain, but in its looks and responsiveness. 

Here, screens find their destiny. Always in twos they emerge, no more, no less - **Compose UI** and `ComposeViewModel`, their names be. [MVI](https://staltz.com/unidirectional-user-interface-architectures.html) I have heard.

## Modularization: by screen/feature

Split our app into many modules, we do. Tangled webs ([spaghetti code](https://en.wikipedia.org/wiki/Spaghetti_code), [coupling](https://en.wikipedia.org/wiki/Coupling_(computer_programming))) we avoid, swift builds we seek. Furthermore, permits many hands to craft yet, in peace they work, not clashing ([merge conflicts](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/addressing-merge-conflicts/about-merge-conflicts)).

Each screen, like its own planet it is. Expanding it might, with tales of code and tales. But in its own orbit, it stays. The galaxy of code, peaceful and unshaken it remains.

![modularization-strategy](../assets/modularization.svg)

**Simple, our modularization path is:**

- Shared `:ivy-*` modules, a few we possess.
- With these modules, shared wisdom in your screens you harness.
- Each screen/feature, its own realm it claims, like `:screen-home` or `:feature-something`.

### Creating a new module

To birth a new module... Invoke this chant in your terminal, you must:
```
./scripts/create_module.sh screen-something
```

> Crafting a screen, you are? Inscribe its essence, you must, in `Screens.kt` (:ivy-navigation). Weave its spirit, you shall, in `IvyNavGraph.kt` (:app).

## Wisdoms from Far and Wide

["The Grug Brained Developer"](https://grugbrain.dev/), seek this scroll, you must. Tales of old engineering and fun, within it rests.

Legacy code, surround us it does. To restore balance to the code force, visit ["devexp" issues](https://github.com/Ivy-Apps/ivy-wallet/labels/devexp) you must.

## Uncertain, you are?

Seek knowledge in the "Development" realm of our [Telegram sanctuary](https://t.me/+ETavgioAvWg4NThk), you must. There, answers await.

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)
