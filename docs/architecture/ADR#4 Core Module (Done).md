# Architecture Decision Record (ADR) #4: `:core` module and modules re-structure ✅

## _This ADR is WIP and will likely change..._

After executing [ADR#1: Modularization](ADR#1%20Modularization%20(Done).md) we ended up with confusing modules structure and a lot of `:temp`, `:app-base` and other "workaround" modules required to build the app successfully.

We plan to address that by:
1) Organize our modules better.
2) Leverage submodules (e.g. `:core:actions`).

## Problem

After the migration to modularized migration we ended up with a [big ball of mud](https://en.wikipedia.org/wiki/Big_ball_of_mud) scattered around multiple modules w/o logical structure.
- complexity++
- coupling.
- hard for developers to add the right dependencies to new modules.
- a lot of unnecessary dependencies.
- hard to navigate the project.
- slower build times.
- it's bad!

## Solution

Create a logical structure of modules and submodules having in mind that one-day we may want to migrate this project to [KMP (Kotlin Multiplatform)](https://kotlinlang.org/docs/multiplatform.html).

**Goal:**
- reduce the number of root modules.
- make navigation easier.
- remove coupling and unncessary dependencies.
- reduce complexity.

### 1) Domain module `:core`.
- `:core:data-domain`: Ivy's data model [`Transaction`, `Account`, `Category`, ...]
- `:core:data-ui`: transformation of `data-domain` so it's optmized for UI and Jetpack Compose
- `:core:functions`: pure functions
- `:core:actions`: re-usable use-cases for domain logic
- `:core:ui`: re-usable UI components providing extension functions for `data-ui`
- `:core:persistence`: handling local persistence RoomDB

### 2) Move all resources [strings, drawables, ...] to `:resources`.
No code module, containing only resources. Why?
- It'll make it easy to access all Ivy icons and strings when you develop.
- It'll be easy for contributors to manage `strings.xml` and translations.
- R8 will optimize so there's no big cost.

### 3) Remove `:screens` and migrate to `:navigation` 
See [ADR#3: Jetpack Navigation](ADR#3%20Jetpack%20Navigation%20(WIP).md). TL;DR;
- define all possible nav destinations in `:navigation`.
- wire nav destionations with @Composable UI in `:app`

### 4) Other key modules
- `:common`: light weight module used to provide common deps for any module.
- `:design-system`: Ivy Design system
- `:sync`: logic for syncing your data to Ivy Cloud (deprecated) and soon Google Drive

### 4) Continue grouping scattered modules in bigger modules composed of submodules.

### 5) Modules to remove
- `:state` because `Flow` will handle it under the hood.
- all `temp-*` modules.
- `:ui-common` -> `:design-system`.
- `:ui-components-old` -> `:core:ui`.
- `:app-base` - decouple and remove.
- `:data-model` -> `:core:data-domain`.

## Benefits
- clear module structure.
- de-coupling.
- easier navigation between modules.
- faster builds.
- reduces overall complexity.