[![Latest Release](https://img.shields.io/github/v/release/iliyangermanov/ivy-wallet)](https://github.com/ILIYANGERMANOV/ivy-wallet/releases)
[![TEST](https://github.com/ILIYANGERMANOV/ivy-wallet/actions/workflows/test.yml/badge.svg)](https://github.com/ILIYANGERMANOV/ivy-wallet/actions/workflows/lint.yml)
[![Build](https://github.com/ILIYANGERMANOV/ivy-wallet/actions/workflows/build.yml/badge.svg)](https://github.com/ILIYANGERMANOV/ivy-wallet/actions/workflows/internal_release.yml)

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/ILIYANGERMANOV/ivy-wallet/blob/main/CONTRIBUTING.md)

# [Ivy Wallet: money manager](https://play.google.com/store/apps/details?id=com.ivy.wallet)

Personal finance and spending tracker app which helps you manage your money.

<div align="center">
<img src="https://play-lh.googleusercontent.com/fxNeMm4BRWJ4ZozX5m8CSBbjZhcx0rKJM2cbiTgjKw4zxt8Pf_2BZBWp5L19R0XFdg=w720-h310-rw" alt="ivy-wallet-money-manager">
<img src="https://play-lh.googleusercontent.com/gtQQvucrc1dU7KRWj8iZW9n24aw_qY8M0W2J_rFLeYf9WVCyIL-hAWAz3mkcjTnNmw=w720-h310-rw" alt="ivy-wallet-spending-tracker">
<img src="https://play-lh.googleusercontent.com/dFuAvUVBF8wCB-yAcDQQDbrAJNXF_l-gy__yhB5MK9l7e7-NtTgvMh1U7UEC5PC06u7M=w720-h310-rw" alt="ivy-wallet-expense-tracker">
<img src="https://play-lh.googleusercontent.com/EmHhqdjylPK9K5Wh39vPFzUewNXOMNSNdVKhQze1G36mfm-ZEEqcbpzVU0bqX9MA5A=w720-h310-rw" alt="ivy-wallet-screenshot-4">
<img src="https://play-lh.googleusercontent.com/BCvcfsvWvomB3K24ZKxRgQ2Wvj8HlP-q1gXHwD8ShXlUPFzWGNVn5bMHos2tO3z-0w=w720-h310-rw" alt="ivy-wallet-screenshot-5"/>
<img src="https://play-lh.googleusercontent.com/bItT3JQsWq1iBcN7EpW4ceSgVuVzQUuIQX4zd2ZME7eXrHXEM1_vgUkeQaL56LTSqao=w720-h310-rw" alt="ivy-wallet-screenshot-6"/>
<img src="https://play-lh.googleusercontent.com/FqosJyCWk4IlalUubEuwXNIeyCABavgZ4C523rfwnM7VgO0ABDT7hWvlhQIDbxO41iI=w720-h310-rw" alt="ivy-wallet-screenshot-7"/>
<img src="https://play-lh.googleusercontent.com/BwrC-_rmIkmy0od5ebHh_8IvLoTV7-Ci3-M5cPQ62Q9ZeSI3CBWR8OHtajXAEOTFN24=w720-h310-rw" alt="ivy-wallet-screenshot-8"/>
</div>

Ivy Wallet is a free budget manager and spending tracker app that’ll help you manage your personal finances with ease.

Imagine Ivy Wallet as a digital financial notebook (manual expense tracker) in which you’ll track your income, expenses, and budget.

You'll get an answer to these three essential questions:

1) Exactly how much money do I have right now in all accounts combined? _(money manager)_

2) How much did I spend this month and where? _(expense tracker)_

3) How much money can I spend and still reach my financial goals? _(budget manager)_

<a href='https://play.google.com/store/apps/details?id=com.ivy.wallet&utm_source=github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

## Ideology :earth_africa:

We believe that people _(not corporations or business entities)_ can create innovative, open-source,
and free software that can make the world a better place.

**We want Ivy to be:**

- A place where you can excel and have fun while contributing to something meaningful.
- A community where you can express yourself freely and build the future that you want to live in.
- An open-source project with zero-tolerance to "bad" code and putting code quality first. 

**We believe in:**

- Freedom.
- Creativity & Innovation.
- Challenging the status quo.
- Technical excellence and **eliminating complexity at any cost**.
- Decentralization.
- Having the right to do what you believe in, not what you're told to.

## Community

Be the change! Join our [Telegram community](https://t.me/+ETavgioAvWg4NThk), comment on GitHub, and
tell us how we can create a better environment for developers & creators to work together.

### [Ivy Telegram Community](https://t.me/+ETavgioAvWg4NThk)

### [Ivy Telegram News](https://t.me/ivywallet)

## Architecture

Our goal is to make this repo the **go-to project to learn about Android Development best practices** and experience deligthful software architecture and design.

We're far from this and we live in an every changing dynamic world => our software design will change a lot! :rocket:

But fear not, we'll document major design change as ADRs (Architecture Decision Records) in **[docs/architecture](docs/architecture/)**.

> The best things of ADRs that you'll also be able to see our wrong decisions and how we fixed them!

We're also linking great learning materials (books, videos, articles, papers) in **[docs/resources](docs/resources/)**.

> Have ideas how we can make our code better? [![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/ILIYANGERMANOV/ivy-wallet/blob/main/CONTRIBUTING.md)

## Tech Stack

### Paradigms
- Modular architecture
- [FRP (Functional Reactive Programming)](https://www.toptal.com/android/functional-reactive-programming-part-1)
- MVVM

### Core
- 100% [Kotlin](https://kotlinlang.org/)
- 100% [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
- [Hilt](https://dagger.dev/hilt/) (DI)
- [Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation)
- [ArrowKt](https://arrow-kt.io/) (Functional Programming)
### Networking
- [Retrofit](https://square.github.io/retrofit/) (REST)
- [OkHttp3](https://square.github.io/okhttp/) (REST client)
- [Gson](https://github.com/google/gson) (JSON serialization)
### Local Persistence
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (key-value storage, Shared Preferences replacement)
- [Room DB](https://developer.android.com/training/data-storage/room) (SQLite ORM)
### Other
- [Timber](https://github.com/JakeWharton/timber) (Logging)
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) (crashes, logging)
### CI/CD
- [Gradle KTS](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Fastlane](https://fastlane.tools/) (upload to Google PlayStore)
- [Github Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) (CI/CD)

## Project Requirements
- Java 11+
- Android Studio Dolphin+ (for easy install use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

## How to build?
1. Clone the repository
2. Open with Android Studio
3. Everything should sync and build automatically
- _If any build problems occurr, please [open a new issue](https://github.com/Ivy-Apps/ivy-wallet/issues/new?assignees=&labels=dev&template=dev-contributor-request.yml) including the logs._

## Contributors [(see graph)](https://github.com/ILIYANGERMANOV/ivy-wallet/graphs/contributors)

> To support our open-source project, please ⭐ this repo - that means a lot for us! Thank you! 🙏

### Why to contribute?
- It's a win-win!
- You'll appear as an Ivy Wallet contributor in our public repo
- You can **include it in your CV/LinkedIn** and show recruiters that you contribute to our app _(counts as +1 released app in the [Google PlayStore](https://play.google.com/store/apps/details?id=com.ivy.wallet))_
- You'll make Ivy Wallet better
- You can develop the features that you want yourself, the way you want them
- You'll play around and learn cutting-edge technologies
- It's the easiest way to learn [Jetpack Compose](https://developer.android.com/jetpack/compose) in
  a production environment
- You can see Android Development Best Practices in 2022 (and also help us improve our code)
- When we merge your first PR
  I ([Iliyan Germanov](https://www.linkedin.com/in/iliyan-germanov-3963b5b9/)) will endorse you on
  LinkedIn for Android Development and Kotlin
- Personal recommendation on your LinkedIn profile after 10 successfully merged PRs - make sure that
  you notify us because we don't count your PRs :)

### How to contribute?

Follow our
compact **[Contributors Guide](https://github.com/ILIYANGERMANOV/ivy-wallet/blob/main/CONTRIBUTING.md)**
to begin.

TL;DR:
- Submit pull requests for bug fixes / code improvements to the `develop` branch
- Implement and submit PRs for opened issues
- Report (or fix) bugs/glitches 
- Create new issues to give us ideas and feedback
- [Download Ivy Wallet](https://play.google.com/store/apps/details?id=com.ivy.wallet) and leave us a review

I hope a lot more profile pictures are going to show up here, soon!

### Contributors Wall:
<a href="https://github.com/ILIYANGERMANOV/ivy-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ILIYANGERMANOV/ivy-wallet" />
</a>
<br>
<br>

_Note: It may take up to 24h for the [contrib.rocks](https://contrib.rocks/preview?repo=ILIYANGERMANOV%2Fivy-wallet) plugin to update because it's refreshed once a day._ 
