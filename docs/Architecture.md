# Ivy's Architecture

Ivy Wallet's architecture is simple - do the shortest solution that works and doesn't break the app. We are no fanatics/sectarians that follow strict rules/patterns/paradigms - we do whatever makes sense.

Although there a few principles that we like:

- **80/20:** 20% of the code brings 80% of the user value
- **Don't walk away from complexity, run!** If you can't explain it to a 5 yr old, delete it and start from scratch.
- **Don't overengineer.** Less is more. The best developers come up with the "dumbest" solutions. Be pragmactic.
- **Build for today cuz tomorrow may never come.** Building for the future is a sure way to fail in the present.

Enough philosophy, just be yourself - keep it simple and let's dive to the details.

## Screen: Compose UI + ComposeViewModel

We separate the Compose UI from the screen logic because of:
1. **Jetpack Compose perfomance:** Uses a Compose-optimized `UiState`

![Screen-Viewmodel](../assets/screen-vm.svg)

**How it works? TL;DR;**
- **Sceen (UI):** very dumb. Displays a snapshot of a **UiState** and sends user interaction events as **UiEven** to the **ViewModel**.
- **UiState:** usually a `data class` with a bunch of primitive `val`s that are displayed in the UI. _Note: The `UiState` must be optimized for Compose and contain only **@Immutable** structures so Compose can run efficiently._
- **UiEvent:** defines all possible user interections _(e.g. user clicks a button, enters text, checks a checkbox, slides a slider)_
- **ViewModel:** produces the current `UiState` and handles `UiEvents`. Encapsulates the screen's logic and does the CRUD/IO operations.

**Compose in the ViewModel - Why?**


## Modularization: by screen/feature

## Paradigm: pragmatic

## Clean code: NO 🚫

