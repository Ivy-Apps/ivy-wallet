# Architecture Decision Record (ADR) #1: Modularization ✅

Split the `:app` monolith into many small modules by feature or responsibility.

**Goal:** divide, refactor and conquer.

## Problem

Ivy Wallet is progessively becoming bigger and bigger by introducing new features and user requests.
With each new thing added the code becomes more and more coupled leading to higher complexity, harder refactoring,
and more bugs & glitches.

## Solution

Split the `:app` logically by:
- **feature modules:** e.g. `:home`, `:loans`, `:accounts` ...
- **responsibility (shared) modules:** e.g. `:exchange`, `:ui-components`, `:persitence`, `:network`, ...

### Benefits
- **No coupling:** you can't import functions or classes from `:app` or other feature modules.
- **Less complexity:** to re-use code it have to defined-well in a shared module.
- **Faster build time:** if done well only the affected modules will be recompiled and NOT the entire monolith.
- **Stable way to scale and add new feautres:** when developing new features (e.g. Goals) contributors can add
a new module `:goals` and develop there w/o risk of breaking `:categories`, `:accounts` or core functionality.
- **Easier maintenance:** problematic modules can be excluded and they won't break other features.
- **Faster unit & UI tests:** we can optimize the CI to run only the tests for the changed modules.

### Drawbacks
- **A lot of work required:** modularization is big iniciative and requires a lof ot work 
because of our coupled code in `:app`.
- **Will break R8:** we have to redesign how are `minify` work.
- **Will break R (resources):** as our resources are monolithic it'll take some time to de-couple and re-organize them.
- **Complex Gradle KTS setup:** compared to a monolith is way harder to configure all gradle modules
and their dependencies efficiently.
- **Learning curve for contributors:** contributors will have to understand our modules structure, 
how to create modules and how to navigate between them.
- **Until fully completed it's worse than a monolith:** a lot of `temp` modules or junkyards like `app-base`.

### Alternatives

- Stay with `:app` monolith.
- Modularize only by domain - `:ui`, `:actions`, `:persistence`, `:network`, ...

## Implementation Strategy

### When to create a new module?
- When a group of code or resources can be logically organized and isolated
from other dependencies. Good examples:
  - `:exchange`: holds all exchange logic (created)
  - `:date-time`: holds all datetime conversion and formatting code (not created)
  - `:backups`: all logic for JSON zip backs - import & export. 
(not created- @Vishwa)

- When code or components from a monolithic "trashy" `:temp-` modules can
be logically extracted. Example `:ui-components-old` -> `:modals`
-> `:account-modals`, `:category-modals`, `:loan-modals`, ..

- When large feature modules can logically split futher. Example `:main` ->
`:more-menu`, `:customer-journey`, `:home`, `:accounts`.

> ❗To create a new module run `runhaskel scripts/create_module.hs`.

### When to move "stuff" to another module?

- When a code in a shared module is only used in one feature module. 
Example: `CategoryAmount` data class in `:app-base` -> `:pie-charts`.

- When a code in a shared module can be used only in a few logically connected feature modulues.

- When a code in a monolithic shared module can be isolated further.
Example: The modals UI from `:ui-components-old`to `:modals` or even further to many smaller modals
modules like `:amount-input-modals`, ...

### Common module dependencies
- `:common`: contains all common Kotlin deps
- `:ui-common`: `:common` + Jetpack Compose + other UI related stuff
- `:data-model`: The data model behind Ivy Wallet. ⚠️ Add only data classes w/o methods in it -> 
module should NOT contain behavior. (we'll cover it another ADR)
- `:temp-persistence`: everything persistence related => will be split further
- `:temp-domain`: all Actions, pure functions and "Logic" => will be split further
- `:app-base`: all resources + random stuff - must be reworked
- `:ui-components-old`: all legacy Ivy Wallet components => will be split and deprecated.
- `:screens`: data classes for all screens in the app, used for navigation

### Temporary (trashy) modules

This modules are just bad but we temporarily need them to have the project compiling. 
If you want to help consider splitting them logically further.

- `:app-base`
- `:temp-domain`
- `:ui-components-old`
- `:temp-network`
- `:temp-persistence`

## Further Reading
(TODO: Add resources)
- [Resource 1]()
- [Resource 2]()
- ...
