# Architecture Decision Record (ADR) #5: New data model ✅

Remove `TransactionType.TRANSFER` and support only `Income` and `Expense` transctions. Represent `TRANSFER` with `TransactionBatch` which is a group of [`Transaction`].

## Solution

1) Remove `TransctionType.Transfer`

2) Create `linked_trns` table:
- id: UUID (batch id)
- linkedTrns: [UUID]

3) Add `purpose: BatchPurpose?` to `Transaction` table:
- BatchPurpose: From | To | Fee (can be extended with more types any time) 

4) Create `BatchTrnsFlow`

5) Simplify all `:core:actions` using `TransactionType.Transfer`

6) Create UI for representing `TrnsBatch` as Transfer transaction card.


## Benefits
- support Transfer fees.
- more flexible and flexible data-model (can support taxes, etc)
- simpler business logic.
- removes `treatTransfersAsIncExp` complex settings => simpler UX.
- reduce complexity.

## Drawbacks
- data migration might be tricky.
- Ivy Cloud won't support it.
- `TrnsBatchFlow` might be tricky.
- Deleting batched .transactions should update `linked_trns` table.