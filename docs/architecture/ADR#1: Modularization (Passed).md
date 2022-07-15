# Architecture Decision Record (ADR) #1: Modularization

Split the `:app` monolith into many small modules by feature or responsibility.

## Problem

Ivy Wallet is progessively becoming bigger and bigger by introducing new features and user requests.
With each new thing added the code becomes more and more coupled leading to higher complexity, harder refactoring,
and more bugs & glitches.

## Solution

Split the `:app` logically by:
- **feature modules:** e.g. `:home`, `:loans`, `:accounts` ...
- **responsibility (shared) modules:** e.g. `:exchange`, `:ui-components`, `:persitence`, `:network`, ...

### Benefits
- **No couplicing:** you can't import functions or classes from `:app` or other feature modules.
- **Less complexity:** to re-use code it have to defined-well in a shared module.
- **Faster build time:** if done well only the affected modules will be recompiled and NOT the entire monolith.
- **Stable way to scale and add new feautres:** when developing new features (e.g. Goals) contributors can add
a new module `:goals` and develop there w/o risk of breaking `:categories`, `:accounts` or core functionality.
- **Easier maintenance:** problematic modules can be excluded and they won't break other features.
- **Faster unit & UI tests:** we can optimize the CI to run only the tests for the changed modules.

### Drawbacks

### Alternatives

## Implementation

**Rule of thumb:**
