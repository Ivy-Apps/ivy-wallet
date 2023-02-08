# Sync Algo

# _🚧 WIP... 🚧_

An algorithm for syncing Ivy Wallet's data between multiple devices without central authority.

- Fetch the entire JSON zip from Google Drive.
- Fetch all local ids + last_updated

## Things to write locally

- id in Drive && id !in Local
- id in Drive && id in local && drive.last_updated > local.last_updated

## Things to write to Drive

- id !in Drive && id in Local
- in 