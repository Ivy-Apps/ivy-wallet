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


export-csv(:export-csv)
export-zip(:export-zip)
import-csv(:import:csv)
import-zip(:import-zip)

import-csv-app-ivy(:import-csv-app-ivy)
import-csv-app-budgetbakers(:import-csv-app-budgetbakers)

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
ui-planned-payments(:ui-planned-payments)

ui-balance-transactions(:ui-balance-transactions)

ui-home --> app
ui-balance-transactions --> app

ui-onboarding --> app
ui-settings --> app

ui-transaction-details --> app
ui-accounts --> app
ui-categories --> app
ui-pie-chart --> app
ui-more-menu --> app
ui-budgets --> app
ui-loans --> app
ui-reports --> app
ui-planned-payments --> app


ui-customer-journey --> ui-home

ui-component-trn-card --> ui-home
ui-component-trn-card --> ui-balance-transactions

import-csv-app-ivy --> import-csv
import-csv-app-budgetbakers --> import-csv
import-csv --> ui-import
import-zip --> ui-import

export-zip --> ui-export
export-csv --> ui-export

ui-import --> ui-settings
ui-import --> ui-onboarding
ui-export --> ui-settings


```
