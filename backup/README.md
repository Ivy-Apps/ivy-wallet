# Backup

**Purpose:**
- Import data from the old app
- Import data from the new app
- Export data the new app's data

**Structure:**
- `backup:base`: shared module between for the inner implementations
- `backup:old`: import data from the old app
- `backup:impl`: export & import data from the new app ("new" can't be used as a package name)
- `backup:api`: public API for the backup module (other modules will add dependency only to this module)