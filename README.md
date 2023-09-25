[![Latest Release](https://img.shields.io/github/v/release/Ivy-Apps/ivy-wallet)](https://github.com/Ivy-Apps/ivy-wallet/releases)
[![APK](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/apk.yml/badge.svg)](https://github.com/Ivy-Apps/ivy-wallet/actions/workflows/apk.yml)
[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)
[![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers)
[![Fork Ivy Wallet](https://img.shields.io/github/forks/Ivy-Apps/ivy-wallet?logo=github&style=social)](https://github.com/Ivy-Apps/ivy-wallet/fork)

# [Ivy Wallet: money manager](https://play.google.com/store/apps/details?id=com.ivy.wallet)

|          |             |                |       |
| :---:    |    :----:   |          :---: | :---: |
| ![1](https://user-images.githubusercontent.com/5564499/189540998-4d6cdcd3-ab4d-40f7-85d4-c82fe8a017d1.png) | ![2](https://user-images.githubusercontent.com/5564499/189541011-1ebbd8b6-50fe-432a-91e2-59206efe99ce.png) | ![3](https://user-images.githubusercontent.com/5564499/189541023-35e7f163-d639-4466-9a91-c56890d5a28e.png) | ![4](https://user-images.githubusercontent.com/5564499/189541027-d352314c-fd5c-43eb-82ad-4aba14c7b0fa.png)
| ![5](https://user-images.githubusercontent.com/5564499/189541030-1a0d7948-33af-420b-b126-936d0211c93f.png) | ![6](https://user-images.githubusercontent.com/5564499/189541035-621c4511-5ec7-4d3f-b08e-925d8da95472.png) |![7](https://user-images.githubusercontent.com/5564499/189541127-7adf5bfa-0652-461c-80f1-076b7179eb6c.png) | ![8](https://user-images.githubusercontent.com/5564499/189541040-7cab633e-be4c-40b2-a2c6-890a15edf805.png)

Ivy Wallet is a free and open-source **money manager android app**. It's written using **100% Kotlin and Jetpack Compose**. It's designed to help you track your personal finance with ease.

Imagine Ivy Wallet as a manual expense tracker that tries to replace the good old spreadsheet for managing your finance.

**Do you know? Ask yourself.**

1) How much money do I have in total?

2) How much did I spend this month and where?

3) How much money can I spend and still reach my financial goals?

A money manager app can help you answer these questions.

Ivy Wallet lacks features but its biggest advantage is its UI/UX, simplicity, and customization. This was recognized in the ["Top/Best Android App in 2021/2022 charts"](https://youtube.com/playlist?list=PLguJN0waG1-eSzKMuFMIULrR3MlqJ3cAE) by the YouTube tech community.

<a href='https://play.google.com/store/apps/details?id=com.ivy.wallet&utm_source=github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="323" height="125"/></a>

> Join our **[private Telegram Community](https://t.me/+ETavgioAvWg4NThk)**.

> To support our free open-source project please give it a star. ⭐
> That means a lot for us. Thank you! [![GitHub Repo stars](https://img.shields.io/github/stars/Ivy-Apps/ivy-wallet?style=social)](https://github.com/Ivy-Apps/ivy-wallet/stargazers)

## Project Requirements

- Java 17+
- The **latest stable** Android Studio (for easy install use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

### Initialize the project

**1. Fork and clone the repo**

Instructions in [CONTRIBUTING.md](./CONTRIBUTING.md).

**2. Make the initialization script executable:**
```
chmod +x scripts/init.sh
```

**3. Initialize the project:**
```
./scripts/init.sh
```

### Need help?

Jour our Telegram community and drop a message in the "Development" topic.

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)

## Learning Materials

Ivy Wallet is a great place to code and learn. That's why we're also linking great learning materials (books, articles, videos), check them in **[docs/resources 📚](docs/resources/)**.

Make sure to also check our short **[Architecture Guidelines 🏗️](docs/Architecture.md)** to learn more about the Ivy Wallet's tech side.

[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md)

## Tech Stack

### Core

- 100% [Kotlin](https://kotlinlang.org/)
- 100% [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material3 design](https://m3.material.io/) (UI components)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) (structured concurrency)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html) (reactive datastream)
- [Hilt](https://dagger.dev/hilt/) (DI)
- [ArrowKt](https://arrow-kt.io/) (functional programming)
- [Kotest](https://kotest.io/) (unit testing)

### Local Persistence
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (key-value storage)
- [Room DB](https://developer.android.com/training/data-storage/room) (SQLite ORM)

### Networking
- [Ktor Client](https://ktor.io/docs/getting-started-ktor-client.html) (REST client)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) (JSON serialization)

### Build & CI
- [Gradle KTS](https://docs.gradle.org/current/userguide/kotlin_dsl.html) (Kotlin DSL)
- [Gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html) (build logic)
- [Gradle version catalogs](https://developer.android.com/build/migrate-to-catalogs) (dependencies versions)
- [Fastlane](https://fastlane.tools/) (uploads the app to the Google PlayStore)
- [Github Actions](https://github.com/Ivy-Apps/ivy-wallet/actions) (CI/CD)

### Other
- [Firebase Crashlytics](https://firebase.google.com/products/crashlytics) (stability monitoring)
- [Timber](https://github.com/JakeWharton/timber) (logging)
- [Detekt](https://github.com/detekt/detekt) (linter)
- [Ktlint](https://github.com/pinterest/ktlint) (linter)
- [Slack's compose-lints](https://slackhq.github.io/compose-lints/) (linter)

## Contribute

**Want to contribute?** See **[CONTRIBUTING.md](/CONTRIBUTING.md)** [![Fork Ivy Wallet](https://img.shields.io/github/forks/Ivy-Apps/ivy-wallet?logo=github&style=social)](https://github.com/Ivy-Apps/ivy-wallet/fork)

### Contributors Wall:

<a href="https://github.com/ILIYANGERMANOV/ivy-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Ivy-Apps/ivy-wallet" />
</a>
<br>
<br>

_Note: It may take up to 24h for the [contrib.rocks](https://contrib.rocks/preview?repo=Ivy-Apps%2Fivy-wallet) plugin to update because it's refreshed once a day._ 

**P.S.** You'll also be recognized in Ivy Wallet's "Contributors" screen that can be found in the android app. We salute you! 👏

## Creative Contributors

Folks that helped Ivy Wallet in a non-dev creative way that can't be captured in GitHub.

### Creative Contributors Wall:

<!-- <div align="center">
  <a href="URL_TO_CONTRIBUTION">
    <img src="URL_TO_PERSONS_PHOTO" width="100px;" alt="PERSON'S PHOTO"/><br>
    <strong>USERNAME</strong><br>
    <small>MESSAGE_FOR_THEIR_CONTRIBUTION</small>
  </a>
</div> -->

<div align="center">
    <img src="https://media.licdn.com/dms/image/D4D03AQGiFQMobe7CmA/profile-displayphoto-shrink_400_400/0/1690718245199?e=1698883200&v=beta&t=rMTp7gOVZKqWx3Dcj6vKGkjwKgKi5NShXEXMRtne8KU" width="100px;" alt="Stefan Ilijev - desinger"/><br>
    <strong>Stefan Ilijev</strong><br>
    <small>Ivy Wallet's co-founder and designer. Created the <a href="https://www.figma.com/file/kSwIa07jcHEHZXo6rzx7dn/Design-System?node-id=0%3A1&mode=dev">Ivy Wallet design.</a></small>
    <br/>
    <br/>
</div>

<div align="center">
    <img src="https://cdn.aditya.tk/img/aditya.jpg" width="100px;" alt="Aditya [ADX]"/><br>
    <strong><a href="https://aditya.tk" >Aditya</a> </strong><br>
    <br/>
</div>

<div align="center">
    <img src="https://avatars.githubusercontent.com/u/130169485?v=4" width="100px;" alt="Shymom [SSI]"/><br>
    <strong><a href="https://github.com/SHYMOM" >Shymom</a> </strong><br>
    <br/>
</div>


