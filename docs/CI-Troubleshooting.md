# Troubleshooting CI failures

If you see any of the PR checks failing (❌) go to [Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) and find it there. Or simply click "Details" next to the failed check and explore the logs to see why it has failed.

## PR description check

It means that you didn't follow our [official PR template](../.github/PULL_REQUEST_TEMPLATE.md).
Update your PR description with all necessary information. You can also check the exact error by
clicking "Details" on the failing (❌) check.

## Detekt
[Detekt](https://detekt.dev/) is a static code analyzer for Kotlin that we use to enforce code readability and good practices.

**To run Detekt locally:**
```
./gradlew detekt
```

If the Detekt errors are caused by a legacy code, you can suppress them using a baseline.

**Suppress Detekt** (only if you're sure that Detekt is wrong)

Add `@Suppress("ID_OF_THE_CHECK")` to ignore the error on a single place. For example:
```kotlin
@Suppress("FunctionMaxLength")
fun veryVeryLongFunction() {
    // ...
}
```

**Detekt baseline** (not recommended)
```
./scripts/detektBaseline.sh
```

## Lint

We use the [standard Android Lint](https://developer.android.com/studio/write/lint) plus [Slack's compose-lints](https://slackhq.github.io/compose-lints/) as an addition to enforce proper Compose usage.

**To run Lint locally:**
```
./scripts/lint.sh
```

If the Lint errors are caused by a legacy code, you can suppress them using a baseline.

**Suppress Lint** (only if you're sure that Lint is wrong)

Same as suppressing Detekt, just add `@Suppress("ID_OF_CHECK")`.

**Lint baseline** (not recommended)
```
./scripts/lintBaseline.sh
```

## Unit tests

If this job is failing this means that your changes break an existing unit test. You must identify the failing tests and fix your code.

**To run the Unit tests locally:**
```
./gradlew testDebugUnitTest
```

## Compose Stability

This GitHub Action checks whether your `@Composable` functions are stable (i.e. "restartable" and "skippable"). If it fails it means that some of your composables are unstable. That causes unnecessary recompositions which can lead to lost frames and laggy UI/UX especially when animation or scrolling. You must fix that! To fix it, open the failing working and see the output from the report - it tells you which `@Composable` functions are unstable and what parameters cause that.

**Fixing Stability issues:**
1. Read https://developer.android.com/jetpack/compose/performance/stability/fix
2. https://developer.android.com/jetpack/compose/performance/stability

**Compose Stability baseline** (not recommended)
```
./scripts/composeStabilityBaseline.sh
```
Do that only if the failure is in legacy code. If the script is failing, open it and execute the commands inside it manually.


## Paparazzi Tests
Paparazzi is used for automated visual testing. It captures screenshots of various `@Composable` and then compares them against baseline images to detect any visual differences or regressions.

**Fixing Paparazzi issues:**

1. Run `./gradlew verifyPaparazziDebug` locally to execute the Paparazzi tests and identify the specific screens where failures occur.
2. Upon failure, the system generates detailed reports pinpointing the changes introduced since the last successful test run. Review these reports to understand the nature of the failures.
3. If the identified changes are intentional, proceed by running `./gradlew {module_name}:recordPaparazziDebug` to update the baseline screenshots specifically for the failed UI components.
4. After updating the baseline screenshots, rerun `./gradlew {module_name}:verifyPaparazziDebug` to ensure that the tests now pass with the updated baselines.
5. Repeat this process iteratively for all modules that have failed Paparazzi tests, ensuring thorough validation and updating of baseline images as needed.

> **Note**: Ensure the use of static data only. Dynamic data, such as current time or date, will cause test failures due to its variability.