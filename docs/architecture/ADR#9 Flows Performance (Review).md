# ADR#9: Flows Performance 👀

Ivy Wallet loads data turbo slow when there are many transactions. See [Issue#1719](https://github.com/Ivy-Apps/ivy-wallet/issues/1719)... 🐢

## Context

- After importing ~Nk transactions, the "Home" and "Account" tabs in the Main screen started to load extremly slow...
- **Note:** the same data loads instantly in the old Ivy Wallet (`prod` branch) on the PlayStore.

## Problems

- Ivy Wallet consumes ~220MB RAM for just showing the "Home" tab which is bad!
- After filling the available the GC starts collecting lize crazy with the one only result of setting the CPU on fire... Getting exponentially slower:
```
Background young concurrent copying GC freed 616663(21MB) AllocSpace objects, 0(0B) LOS objects, 23% free, 62MB/81MB, paused 2.203ms,66us total 680.350ms
2023-01-12 22:35:34.451  6004-6016  vy.wallet.debug         com.ivy.wallet.debug                 I  Background concurrent copying GC freed 740158(26MB) AllocSpace objects, 0(0B) LOS objects, 28% free, 59MB/83MB, paused 86us,66us total 851.010ms
```

# _WIP... Investigation is continuing and this ADR isn't yet finished..._
