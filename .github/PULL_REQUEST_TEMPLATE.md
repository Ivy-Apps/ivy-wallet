## Pull Request (PR) Checklist
Please check if your pull request fulfills the following requirements:
- [ ] The PR is submitted to the `main` branch.
- [ ] I've read the [Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) and my PR doesn't break the "Contributing Rules".
- [ ] I've read the [Architecture Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/Architecture.md).
- [ ] I confirm that I've run the code locally and everything works as expected.
- [ ] 🎬 I've attached a **screen recoding** of the changes. 

> Tip: drag & drop the video to the PR description.

## What's changed?
<!--
Tip: you can attach screenshots using a markdown table.

Before | After
---|---
image1 | image2
-->

Describe with a few bullets **what's new:**
- a
- b
- c
- d

> 💡 Tip: Please, attach screenshots and screen recordings. It helps a lot!

## Risk Factors

**What may go wrong if we merge your PR?**

- a
- b
- c

**In what cases your code won't work?**

- a
- b
- c


## Does this PR closes any GitHub Issues?

Check **[Ivy Wallet Issues](https://github.com/Ivy-Apps/ivy-wallet/issues)**.

- Closes #ISSUE_NUMBER

> Replace `ISSUE_NUMBER` with your issue number (for example Closes #1234). If you've done that correctly, you'll see the issue title linked when previewing your PR description.

## Troubleshooting CI failures

If you see any of the PR checks failing (❌) go to [Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) and find it there. Or simply click "Details" next to the failed check and explore the logs to see why it has failed.

### Detekt
[Detekt](https://detekt.dev/) is a static code analyzer for Kotlin that we use to enforce code readibility and good practices.

**To run Detekt locally:**
```
./gradlew detekt
```

If the Detekt errors are caused by a legacy code, you can suppress them using a basline.

**Detekt baseline** (not recommended)
```
./scripts/detektBaseline.sh
```

### Lint

We use the [standard Android Lint](https://developer.android.com/studio/write/lint) plus [Slack's compose-lints](https://slackhq.github.io/compose-lints/) as an addition to enforce proper Compose usage.

**To run Lint locally:**
```
./scripts/lint.sh
```

If the Lint errors are caused by a legacy code, you can suppress them using a basline.

**Lint baseline** (not recommended)
```
./scripts/lintBaseline.sh
```

### Unit tests

If this job is failing this means that your changes break an existing unit test. You must identify the failing tests and fix your code.

**To run the Unit tests locally:**
```
./gradlew testDebugUnitTest
```