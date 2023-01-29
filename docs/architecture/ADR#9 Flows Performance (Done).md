# ADR#9: Flows Performance ✅

Ivy Wallet loads data turbo slow when there are many transactions. See [Issue#1719](https://github.com/Ivy-Apps/ivy-wallet/issues/1719)... 🐢

## Context

- After importing ~3k transactions, the "Home" and "Account" tabs in the Main screen started to load extremly slow...
- **Note:** the same data loads instantly in the old Ivy Wallet (`prod` branch) on the PlayStore.

## Problems

- Ivy Wallet consumes ~220MB RAM for just showing the "Home" tab which is bad!
- After filling the available the GC starts collecting lize crazy with the one only result of setting the CPU on fire... Getting exponentially slower:
```
Background young concurrent copying GC freed 616663(21MB) AllocSpace objects, 0(0B) LOS objects, 23% free, 62MB/81MB, paused 2.203ms,66us total 680.350ms
2023-01-12 22:35:34.451  6004-6016  vy.wallet.debug         com.ivy.wallet.debug                 I  Background concurrent copying GC freed 740158(26MB) AllocSpace objects, 0(0B) LOS objects, 28% free, 59MB/83MB, paused 86us,66us total 851.010ms
```

## Memory Eaters
- "Home" and "Account" tabs are in the same Compose navigation graph node => "Accounts" tab spawn many unnecessary ViewModels. **Solution: Make "Home" and "Accounts" different Navigation graph nodes** 
- Unnecessary ViewModels for hidden modals (e.g. Accounts tab: CreateAccModal, CurrencyModal, IconsModal, ColorsModal, Folder modals...) **Solution: Create Modal's ViewModel only when the modal is being shown**)
- Nested flows that makes JOIN in the code. **Solution: Use Room DB Views**

## Bottleneck

> :warning: `combine(trns.map { trnFlow() }` can potentially spawn 3k+ flows and totally destroy our RAM...

Note: the `prod` Ivy Wallet and the `develop` one uses the same algorithm to calculate the balance.

Observe: `prod` runs instant, `develop` doesn't. The main difference is that `develop`'s Transaction fetches and reacts to much more things like:
- Transaction Metadata (`trn_metadata` table)
- Tags (`trn_tags` and `tags` tables)
- Attachments(`attachment` tables)

### The Root of the problem
- `TrnsFlow` when fetching "All-time" transactions to calculate the balance.
- **Tags, TrnMetadata, Attachments:** removing this makes loading from 30s to 1-2s.
```
 private fun mapTransactionEntityFlow(
        accounts: Map<UUID, Account>,
        categories: Map<UUID, Category>,
        trn: TrnEntity,
    ): Flow<Transaction>? {
        val account = accounts[trn.accountId.toUUID()]
            ?: return null

        val trnId = trn.id
        val tagsFlow = trnTagDao.findByTrnId(trnId = trnId)
            .flatMapLatest { trnTags ->
                tagDao.findByTagIds(tagIds = trnTags.map { it.tagId })
            }

        return combine(
            trnMetadataDao.findByTrnId(trnId = trnId),
            tagsFlow,
            attachmentDao.findByAssociatedId(associatedId = trnId)
        ) { metadataEntities, tagEntities, attachmentEntities ->
            Transaction(
                ....
            )
        }
    }
```

The above is done for all N transactions multiple M times (M is [3,7]) because of abstraction and inefficient algorithms.

**Why it's called multiple times?**
- "Home tab" and "Accounts tab" load simultaneously.
- `HomeViewModel` fetches TrnsList (shown in history) and transactions twice because of the hiding transactions for the day feature.
- Other inefficiencies... hidden by our Flows abstraction.

## Solution

The Flows abstraction lowers complexity but hides important implementation details - the bottleneck.

From Computer Science perspective we should fetch and use only the information that's 100% neccessary, not waste RAM and time on things that we don't need in the pipeline.

To come-up with a solution that's both efficient and simple (elegant) we need to see the whole picture. 

That's why to make things clean we need to create `docs/algorithms` in the repo where:
- describe the steps of the algorithm
- visualize it with a **diagram**
- optimize it (in theory)
- wrap it in an elegant abstraction (wording)
- finally implement it in the code

With `docs/algorithms` new Ivy Wallet contributors will be able to quickly understand what's happening by looking at the diagrams and also propose improvements.


## Quickfix 

**Problem 1: Calculating stuff (balance, income, expense)**

0. Leave current flows as they are - don't touch them.
1. Create `optimized.CalcTrn`
2. Dao: "SELECT x,y,z... FROM transactions" instead of "SELECT *"
3. `CalcTrnsFlow` will fetch only needed trns data for calculation, no **"tags"**, **"attachemnts"** other BS => in future if more data is needed it can be added to `CalcTrn`
4. React only to what really affects the balance

**Problem 2: Transaction history is slow when you select "All-time"**
- the same concept as Problem 1 but with `HistoryTrn`
