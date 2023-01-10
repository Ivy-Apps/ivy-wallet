# Architecture Decision Record (ADR) #3: Jetpack Navigation ✅

Currently, the Ivy Wallet app uses custom Navigation implementation from our [Ivy FRP](https://github.com/Ivy-Apps/ivy-frp).

Migrating to [Jetpack Navigation](https://developer.android.com/guide/navigation) will provide us many features that our navigation lack like deep links support, nav graph.

## Problem

[Ivy Navigation](https://github.com/Ivy-Apps/ivy-frp/blob/main/frp/src/main/java/com/ivy/frp/view/navigation/Navigation.kt) is inefficient, lack support and developers aren't familiar with it. On the other hand using Google's Jetpack Compose Navigation will solve that and give us access to the features yet to come.

- we need to handle back navigation ourselves which increases complexity.

## Benefits
- Out of the box, back navigation.
- Deep links support.
- Follow [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) best practices.
- Happier developers.
- Actively-developed navigation component.
- Enjoy future optimizations and integrations with Jetpack libraries.