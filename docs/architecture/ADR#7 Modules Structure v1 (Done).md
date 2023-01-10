# Architecture Decision Record (ADR) #7: Modules Structure v1 ✅

Having more info, now we can structure and organize our modules properly.

## :core

Everything the app needs for implementing its "core" purpose - track income/expense & balance.

### :core:data-model

Domain data models designed to be used for business logic's implementation.

### :core:domain

Ivy Wallet's business logic. Flows, actions and functions designed to be used by features.

### :core:persistence

Ivy Wallet's local persistence - `Room DB` and `Datastore`. Designed to be used only
by `:core:domain` and not features.

### :core:ui

UI data models and components for visualing domain data.

## :exchange-rates

Provides latest exchange rates.

## :design-system

Ivy Design system containg Color Science, Typography, design language and key components.

## :navigation

Handles the navigation within the app. Provides a `Navigator` component for changing screens and
dependency inversion between for screen's implementation.

## :sync

🚧 To be defined. 🚧

### :sync:google-drive

### :sync:ivy-cloud

## :backup

🚧 To be defined. 🚧

### :backup:csv

### :backup:json-zip

## :resources

All resources (strings, drawable, styles) used in Ivy Wallet. See [ADR#4: Core Module](ADR%234%20Core%20Module%20(WIP).md). 

## :common

Provides grouping for common dependency and common extensions.

## :common:android-test

Provides groupiing for common test instrumentation (e2e/android) test dependency and useful base classes.

## :android

Groups modules implementing Android SDK features together.

### :android:billing

Google Play Billing implementation.

### :android:notifications

Handling Android notifications.

## Features grouped in modules + submodules

All features will be grouped logically in modules with submodules.

### :home
- :home:customer-jouney
- :home:more-menu
- :home:tab