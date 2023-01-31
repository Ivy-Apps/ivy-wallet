[![Latest Release](https://img.shields.io/github/v/release/Ivy-Apps/ivy-wallet)](https://github.com/Ivy-Apps/ivy-wallet/releases)
[![Build](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/build.yml/badge.svg)](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/build.yml)
[![Lint](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/lint.yml/badge.svg)](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/lint.yml)
[![Unit Test](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/unit_test.yml/badge.svg)](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/unit_test.yml)
<!-- [![Integration Test](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/integration_test.yml/badge.svg)](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/integration_test.yml) -->

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)
[![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers)
[![Github Sponsor](https://img.shields.io/static/v1?label=Sponsor&message=%E2%9D%A4&logo=GitHub&color=%23fe8e86)](https://github.com/sponsors/Ivy-Apps)

# [Ivy Wallet: money manager](https://play.google.com/store/apps/details?id=com.ivy.wallet)

|          |             |                |       |
| :---:    |    :----:   |          :---: | :---: |
| ![1](https://user-images.githubusercontent.com/5564499/189540998-4d6cdcd3-ab4d-40f7-85d4-c82fe8a017d1.png) | ![2](https://user-images.githubusercontent.com/5564499/189541011-1ebbd8b6-50fe-432a-91e2-59206efe99ce.png) | ![3](https://user-images.githubusercontent.com/5564499/189541023-35e7f163-d639-4466-9a91-c56890d5a28e.png) | ![4](https://user-images.githubusercontent.com/5564499/189541027-d352314c-fd5c-43eb-82ad-4aba14c7b0fa.png)
| ![5](https://user-images.githubusercontent.com/5564499/189541030-1a0d7948-33af-420b-b126-936d0211c93f.png) | ![6](https://user-images.githubusercontent.com/5564499/189541035-621c4511-5ec7-4d3f-b08e-925d8da95472.png) |![7](https://user-images.githubusercontent.com/5564499/189541127-7adf5bfa-0652-461c-80f1-076b7179eb6c.png) | ![8](https://user-images.githubusercontent.com/5564499/189541040-7cab633e-be4c-40b2-a2c6-890a15edf805.png)

Ivy Wallet is a **free money manager android app** written using 100% Jetpack Compose and Kotlin. It's designed to help you track your personal finance with ease.

Imagine Ivy Wallet as a manual expense tracker that will replace the good old spreadsheet for managing your personal finance.

Track your expenses, fast and on-the-go! ⚡ Discover powerful insights about your spending.

**Do you know? Ask yourself.**

1) How much money do I have right now in all accounts combined?

2) How much did I spend this month and where?

3) How much money can I spend and still reach my financial goals?

A money manager app can help you answer these questions.

Ivy Wallet's biggest advantage is its UI/UX, simplicity, and customization which was recognized in the ["Top/Best Android App in 2021/2022 charts"](https://youtube.com/playlist?list=PLguJN0waG1-eSzKMuFMIULrR3MlqJ3cAE) 10+ times by the YouTube tech community.

<a href='https://play.google.com/store/apps/details?id=com.ivy.wallet&utm_source=github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="323" height="125"/></a>

> To support our free, open-source project please ⭐ star our repo - that means a lot for us! Thank you! [![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers)
 🙏

> Join our **[private Telegram Community](https://t.me/+ETavgioAvWg4NThk)**.

> You can see our future plans for the product in **[Ivy Wallet's Roadmap](https://github.com/orgs/Ivy-Apps/projects/1)**.

> If you want to support our work see our **[GitHub Sponsors page](https://github.com/sponsors/Ivy-Apps)** [![Github Sponsor](https://img.shields.io/static/v1?label=Sponsor&message=%E2%9D%A4&logo=GitHub&color=%23fe8e86)](https://github.com/sponsors/Ivy-Apps)


## Architecture
We strive to keep our architecture "perfect" by putting software-design and code quality first. 

We read a lot of books, CS papers, blogs and follow latest research in the industry.

Our goal is to make this repo **the go-to project to learn about Android Development latest best
practices** and **software architecture.**

### High-level view:

- [Modular architecture](https://android-developers.googleblog.com/2022/09/announcing-new-guide-to-android-app-modularization.html)
- [FRP (Functional Reactive Programming)](https://www.toptal.com/android/functional-reactive-programming-part-1)
- [MVVM (Model-View-ViewModel)](https://www.techtarget.com/whatis/definition/Model-View-ViewModel#:~:text=Model%2DView%2DViewModel%20(MVVM)%20is%20a%20software%20design,Ken%20Cooper%20and%20John%20Gossman.)
- [Onion Architecture (FP)](https://www.codeguru.com/csharp/understanding-onion-architecture/)

We've documented every major architecture decision as **ADR (Architecture Decision Record)**
in **[docs/architecture](docs/architecture/)**. The best thing about ADRs is that you can see not
only what went well but also what didn't!

We're also big on Computer Science that's why we're documenting every important algorithm used in the app. To see the **algorithms** in Ivy Wallet and their detailed **space-time complexity** analysis go to **[docs/algorithms](docs/algorithms/).**

If you're starting out with the Ivy Wallet project first have a look at our **[:core](core/)**
module.

We're also linking **great learning materials (books, videos, articles, papers)**
in **[docs/resources 📚](docs/resources/)**.

> Have ideas/proposals how we can make our project better? Please, get in touch! 🚀

[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)

## Tech Stack

### Core

- 100% [Kotlin](https://kotlinlang.org/)
- 100% [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
- [Hilt](https://dagger.dev/hilt/) (DI)
- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- [ArrowKt](https://arrow-kt.io/) (Functional Programming)

### Local Persistence
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (key-value storage, Shared Preferences replacement)
- [Room DB](https://developer.android.com/training/data-storage/room) (SQLite ORM)

### Networking
- [Ktor Client](https://ktor.io/docs/getting-started-ktor-client.html) (REST client)
- [Gson](https://github.com/google/gson) (JSON serialization)

### Other
- [Timber](https://github.com/JakeWharton/timber) (Logging)
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) (crashes, logging)

### CI/CD
- [Gradle KTS](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Fastlane](https://fastlane.tools/) (upload to Google PlayStore)
- [Github Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) (CI/CD)


## Project Requirements
- Java 11+
- **Android Studio Electric Eel+** (for easy install
  use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

## How to build?
1. Clone the repository
2. Open with Android Studio
3. Everything should sync and build automatically
- _If any build problems occur, please [open a new issue](https://github.com/Ivy-Apps/ivy-wallet/issues/new?assignees=&labels=dev&template=dev-contributor-request.yml) including the logs._

## Ideology :earth_africa:
We believe that people _(not corporations)_ can create innovative, open-source,
and free software that can make the world a better place.

**We want Ivy to be:**
- A place where you can excel and have fun while contributing to something meaningful.
- A community where you can express yourself freely and build the future that you want to live in.
- An open-source project with zero-tolerance to "bad" code and putting code quality above everything. 

**We believe in:**
- Freedom.
- Creativity & Innovation.
- Challenging the status quo.
- Technical excellence and **eliminating complexity at any cost**.

> We're always open to new ideas and proposals! Have an idea? 💡 Join our [Telegram community](https://t.me/+ETavgioAvWg4NThk) or [submit us a PR](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) - we appreciate both!

## Community
Be the change! Join our [Telegram community](https://t.me/+ETavgioAvWg4NThk), star our GitHub repo [![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers), and
tell us how we can create a better environment for developers & creators to work together.

### [Ivy Telegram Community](https://t.me/+ETavgioAvWg4NThk)

## Contributors [(see graph)](https://github.com/Ivy-Apps/ivy-wallet/graphs/contributors)

### Why to contribute?
- It's a win-win!
- You'll appear in our contributors wall.
- You can **include it in your CV/LinkedIn** and show recruiters that you contribute to open-source projects _(counts as +1 released app in the [Google PlayStore](https://play.google.com/store/apps/details?id=com.ivy.wallet))_.
- You'll make Ivy Wallet better.
- You can develop the features that you miss in the app yourself, the way you want them.
- You'll play around and learn cutting-edge technologies.
- It's the easiest way to learn [Jetpack Compose](https://developer.android.com/jetpack/compose) in
  a production environment.
- You can see Android Development Best Practices in 2022 (and also help us improve our code).

### How to contribute?

Follow our
compact **[Contributors Guide](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)**
to begin.

**TL;DR:**
- Submit pull requests for bug fixes, code improvements and features to the `develop` branch.
- Implement and submit PRs for opened issues.
- Report (or fix) bugs/glitches.
- Create new issues to give us ideas and feedback.
- [Download Ivy Wallet](https://play.google.com/store/apps/details?id=com.ivy.wallet) and leave us a review ⭐⭐⭐⭐⭐.
- Star our GitHub repo [![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet).
- Fix typos in READMEs (.md files), Docs and broken links - we have a lot of them as you'll see and fixing them helps a lot, too!

I hope a lot more profile pictures are going to show up here, soon!

### Contributors Wall:
<a href="https://github.com/Ivy-Apps/ivy-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Ivy-Apps/ivy-wallet" />
</a>
<br>
<br>

_Note: It may take up to 24h for the [contrib.rocks](https://contrib.rocks/preview?repo=Ivy-Apps%2Fivy-wallet) plugin to update because it's refreshed once a day._ 
