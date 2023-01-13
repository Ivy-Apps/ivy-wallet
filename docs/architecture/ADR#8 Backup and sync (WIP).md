# ADR#8: Backup & Sync 🚧

To have a top-notch UX without the Ivy Cloud (own backend) we need to support:
- Automatic offline backups. _(mandatory)_
- Automatic Google Drive backups. _(user's choice)_
- Sharing between multiple users and devices.
- No lag/delays when creating transactions.

## Solution

### Backup Algorithm

1) Export the database entities + user settings as one big JSON.

```
{
    "accounts": [
        ...
    ],
    "categories": [
        ...
    ],
    "transactions": [
        ...
    ],
    "settings": [
        ...
    ]
}
```

2. Create a backup JSON file and ZIP it to reduce it's size.

3. Saved the backup JSON zip to the local file-system

4. _(optional)_ Upload the backup JSON to user's personal Google Drive in "/Ivy-Wallet/backups/ivy-wallet-backup-json.zip".

:warning: When this backup must be triggered?

**Option A): After every write to app's Room Database**
- Pros
  - Google Drive backup will always be up-to-date.
  - even with many transactions the backup file is <1 MB and the operation is quite fast <1 second.
- Cons
  - If user accidentally deletes an account in the app, the backup file will be automatically corrupted.
  - **When making multiple transactions & edits the app will consume enourmous resources!**
  - The app may lag

**Option B): WorkManager - scheduled task every X hours**
- Pros
  - The app won't lag, won't consume extra resources.
  - Elegant and easy to implement solution.
- Cons
  - Data between backups may be lost (not a big deal).
  - Might have Android background work restrictions problems.
  - Will wake-up phone's CPU => consume battery.
  - **No "real-time" sync if multiple users/device use a shared backup file.** 

**Option C) Work manager + trigger backup on app close or X seconds debounce after the last write to DB**
- Pros
  - Elegant like Option B)
  - Solves the "real-time" sync problem
  - Doesn't consume much resources!
- Cons
  - Harder to implement
  - **The backup operation may not finish if they user closes the app before the X debounce seconds**
  - Will consume more resources if the X debounce seconds are low.

### Sync Algorithm

Sync will only happen if the Google Drive integraiton is enabled.
  
_Note: The sync must support multiple users and devices using the same backup JSON file_

1. App opens
2. Fetch the latest "ivy-wallet-backup-json.zip"
3. Merge the data using `ID`s and accepting as newer the ones with greatest `last_synced` in UTC _(!!! `last_synced` must be added to each item in the DB)_

**Actions required**
- add `last_synced` to each item that will be backuped
- don't backup user's settings because different users might want different settings

## Architecture

- `:drive` - drive agnostic API for mountint + CRUD files on user's personal drive, support only Google Drive (`:drive:google-drive`) for v1.

- `:android:file-system` - Android specific logic and permission handling for reading/writing files on user's device including public directories.

- `:backup` - exports/imports users data using JSON backup .zip (`:backup:old` imports JSON backup from the prod app) and the rest will work only with the dev verison.
  - does local file-system backup + Google Drive _(user's preference)_
  - automatic backups uses user-preferred file-system location
  - imports JSON backup .zip files for the old and new app
  - exports a JSON backup to user's chosen location when manually requests

- `:sync` - implement the Sync algorithm and is responsible for merging local app's Room DB + the latest backup JSON from the Drive
