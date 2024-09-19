# Troubleshooting CI failures

If you see any of the PR checks failing (❌) go to [Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) and find it there. Or simply click "Details" next to the failed check and explore the logs to see why it has failed.

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


## Compose preview screenshot testing

Screenshot testing is an effective way to verify how your UI looks to users. The Compose Preview Screenshot Testing tool combines the simplicity and features of composable previews with the productivity gains of running host-side screenshot tests. Compose Preview Screenshot Testing is designed to be as easy to use as composable previews.

A screenshot test is an automated test that takes a screenshot of a piece of UI and then compares it against a previously approved reference image. If the images don't match, the test fails and produces an HTML report to help you compare and find the differences.

We also create an annotation called @IvyPreviews to serve the purpose of exporting and checking images for large-sized devices and small-sized devices.

1. Run `./gradlew validateDebugScreenshotTest` locally to execute the screenshot testing and identify the specific screens where failures occur.
2. Upon failure, the system generates detailed reports pinpointing the changes introduced since the last successful test run. Review these reports to understand the nature of the failures. You can find the report at the following path: {module}/build/reports/screenshotTest/preview/{variant}/index.html
   For example: screen/balance/build/reports/screenshotTest/preview/debug/index.html
3. If the identified changes are intentional, proceed by running `./gradlew :module:update{Variant}ScreenshotTest` to update the baseline screenshots specifically for the failed UI components. For example : `./gradlew :screen:balance:updateDebugScreenshotTest`
4. After updating the baseline screenshots, rerun `./gradlew :module:validate{Variant}ScreenshotTest` to ensure that the tests now pass with the updated baselines. For example : `./gradlew :screen:balance:validateDebugScreenshotTest`
5. Repeat this process iteratively for all modules that have failed compose preview screenshot testing, ensuring thorough validation and updating of baseline images as needed.

> **Note**: Ensure the use of static data only. Dynamic data, such as current time or date, will cause test failures due to its variability.