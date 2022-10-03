## Pull Request (PR) Checklist
Please check if your pull request fulfills the following requirements:
- [ ] The PR is submitted to the `develop` branch.
- [ ] I understand the **[Ivy Developer Guidelines](../docs/Developer-Guidelines.md)**.
- [ ] I've read the **[Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)**.
- [ ] The code builds and is tested on an actual Android device.
- [ ] I confirm that I've run the code locally and everything works as expected.
- [ ] I confirm that I've run Ivy Wallet's UI tests (`androidTest`) and all tests are passing
  successfully.

_Important: Don't worry if you experience flaky UI tests. Just re-run the failed ones again and if they pass => it's all good!_

_Put an `x` in the boxes that apply._
- [x] Demo: Checking checkbox using `[x]`


## Pull Request Type
Please check the type of change your PR introduces:

- [ ] Bugfix
- [ ] Feature
- [ ] Code style update (formatting, new lines, etc.)
- [ ] Refactoring (no functional changes, renaming)
- [ ] Small improvement (fix typo, UI fine-tune, change color or something small)
- [ ] Gradle Build related changes
- [ ] Dependencies update (updating libraries)
- [ ] Documentation (clarifying comments, KDoc)
- [ ] Tests (Unit, Integration, UI tests)
- [ ] Other (please describe):

_Put an `x` in the boxes that apply._


## Does this PR closes any GitHub Issues?
Check **[Ivy Wallet Issues](https://github.com/Ivy-Apps/ivy-wallet/issues)**.
- Closes #N/A (type issue number here)


## What's changed?
Describe with a few bullets **what's new:**
- a
- b
- c
- d

## How to run Ivy Wallet's UI tests (`androidTest`)
**Connect Android Emulator**
- Pixel 5 API 29+ AVD emulator _(recommended)_
- Pixel 3XL API 29+ AVD emulator _(recommended)_
- Large screen physical device _(might also work)_

**Method 1: Android Studio UI**
- Find `com (androidTest)` package
- Right click
- `Run 'Tests in 'com''`

_Note: If you've checked "Compact Middle Packages" the option will appear as `com.ivy.wallet (androidTest)`._

**Method 2: Gradle Wrapper**
- `chmod +x gradlew` (Linux)
- `./gradlew connectedDebugAndroidTest`

**Method 3: Fastlane**
- Install Ruby 2.7
- `bundle install`
- `bundle exec fastlane ui_tests`
