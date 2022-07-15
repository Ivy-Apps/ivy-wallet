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

## Implementation

**Rule of thumb:**

## Further Reading
(TODO: Add resources)
- [Resource 1]()
- [Resource 2]()
- ...
