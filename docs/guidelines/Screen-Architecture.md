# Screen Architecture

Ivy Wallet uses a [Unidirectional Data Flow (UDF)](https://developer.android.com/topic/architecture#unidirectional-data-flow) and MVI architecture pattern with the Compose runtime for reactive state management in the view-model.
It key characteristics are:

![screen-architecture](../assets/screen-vm.svg)

1. The VM produces a single view-state model with all information for the screen.
2. The UI composables directly display the view-state provided by the VM.
3. The user interacts with the Compose UI and the UI transforms user's interactions to events and sends them to the VM.
4. The VM handles the events coming from the UI and produces a new view-state.

Repeat ♻️

## ViewModel

A class that adapts the domain model to a view-state model that the Compose UI can directly display. It combines data from one or many repositories/use-cases and transforms it into a view-state representation consisting of primitives and `@Immutable` structures that composables can draw efficiently.

Let's address the elephant in the room - why use Compose in the ViewModel? The short answer: because it's way more convenient and equally efficient compared to using Flow/LiveData.

### FAQ

**Q: Isn't it an anti-pattern to have Compose and Android/UI logic in the view-model?**

A: Firstly, Compose is more modular than it looks on the surface. `compose.runtime` is very different from the `compose.ui`. In our architecture we use only the Compose runtime as a reactive state management library. The compose runtime state is equivalent to Kotlin Flow but with a simpler, more elegant and powerful API for the purposes of a view-model.

**Q: Don't we couple our view-models with Compose by doing this?**

A: In theory, we couple our ViewModel only with the Compose runtime and its compiler. However, that doesn't matter because:

1. Let's admit it, you'll likely won't change Compose as your UI toolkit anytime soon.
2. If you do change Compose, rewriting the UI composables and components will cost you much more than migrating your view-models, because viewmodels, if done correctly, are very simple adapters of your data/domain layer.

**Q: Can we use Kotlin Flow APIs in a compose viewmodel?**

Yes, we can! And it's very easy to do so:
```kotlin
@Composable
fun getBtcPrice(): String? {
   val btcPrice = remember { someApi.btcPriceFlow }
     collectAsState(initial = null)
   return btcPrice?.let(::format)
}
```

**Q: What's the benefit of having Compose in the VM?**

A: The main benefit is convenience. With the Compose runtime you don't have to do complex Flows like `combine` (limited to 5 flows only), `flapMapLatest` vs `flatMapCombine` and write all the boilerplate code required. Another benefit is that you also have access to the entire Compose runtime API like `remember` (easy memorization), `LaunchedEffect` (execute side-effects under certain conditions), and, ofc, simple, concise, and very readable syntax.

All of the above is better seen in code and practice - make sure to check our references to learn more.

## View-state

The view-state is a data model that contains all information that the screen/composable needs to render its state/design to the user. The view-state is a `data class` or `sealed interface` that contains formatted and ready to display primitives and `@Immutable` data structures.

> Using primitives and immutable data allows the Compose compiler to be smart about what needs to be recomposed, hence makes our composables efficient and smooth af.

## View-event

Our users need to be able to interact with the app and its Compose UI. These interactions include typing input, clicking buttons, gestures, and more. The Compose UI captures these interactions and maps them into view-events that the view-model can easily handle and process.

## Composable UI

The Compose UI is responsible for rendering the view-state according to its design and allowing the user to interact with the UI. The Composable UI listens for user interactions and maps them to events that it sends to the VM.

## References

- [Modern Compose Architecture with Circuit by Slack](https://youtu.be/ZIr_uuN8FEw?si=sulxyqta5dZn-L11)
- [Reactive UI state on Android, starring Compose by Reddit](https://www.reddit.com/r/RedditEng/s/WhIYLJUzNR)
- [The Circuit - Compose-driven Architecture for Kotlin by Slack](https://youtu.be/bMJocp969Bo?si=ab9UrAW1HSwm5sGV)
- [A Jetpack Compose by any other name by Jake Wharton](https://jakewharton.com/a-jetpack-compose-by-any-other-name)
