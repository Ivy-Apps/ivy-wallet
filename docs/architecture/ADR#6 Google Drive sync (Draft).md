# Architecture Decision Record (ADR) #6: Google Drive sync 🗒️

## _Draft: might be rejected at any point 🔴 WIP..._

Drop Ivy Cloud and any custom backend in favor of user managed backup/sync system using Google Drive.

Bold Bet: store data using [Realm Mobile Db](https://realm.io/) and simply export/importing `.realm` DB file to and from Google Drive.

The best thing about `.realm` is that it has cross-platform support and it'll work on Web and iOS, too.