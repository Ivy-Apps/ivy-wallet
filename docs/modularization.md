# Modularization

The goal of this file is to outline how we'll migrate Ivy Wallet from `app` monolith to `modularized` one.

**Motivation:**
- faster builds (only modules with changes will re-build)
- scaleability (create new features w/o affecting existing code)
- easier testing

## Modules Graph

_⚠️ WIP ⚠️_

```mermaid
graph TD;
app(:app)

common(:common)
common-ui(:common-ui)
common-test(:common-test)

network(:network)

export-csv(:export-csv)
export-zip(:export-zip)
import-csv(:import:csv)
import-zip(:import-zip)

import-app-ivy(:import-app-ivy)
import-app-wallet(:import-app-wallet)

ui-component-trn-card(:ui-component-trn-card)


ui-import(:ui-import)
ui-export(:ui-export)

ui-reports(:ui-reports)
ui-transaction-details(:ui-transaction-details)
ui-home(:ui-home)
ui-pie-chart(:ui-pie-chart)
ui-categories(:ui-categories)
ui-accounts(:ui-accounts)
ui-budgets(:ui-budgets)
ui-loans(:ui-loans)
ui-more-menu(:ui-more-menu)
ui-settings(:ui-settings)
ui-onboarding(:ui-onboarding)
ui-customer-journey(:ui-customer-journey)

ui-onboarding --> app
ui-home --> app
ui-transaction-details --> app
ui-accounts --> app
ui-categories --> app
ui-pie-chart --> app
ui-more-menu --> app
ui-settings --> app
ui-budgets --> app
ui-loans --> app
ui-reports --> app


ui-customer-journey --> ui-home

ui-import --> ui-settings
ui-export --> ui-settings




```
