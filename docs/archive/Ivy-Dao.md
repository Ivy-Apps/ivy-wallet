# Ivy DAO

## :warning: WARNING: This business model is deprecated! See [new model](../Ivy-Apps-Business-Model.md) :warning:

## High-level picture
```mermaid
graph TD;
    contribs(Contributors)
    users(Users)
    dao(Ivy DAO)
    product(Ivy Wallet App)

    dao_dev_fund(R&D Fund)
    dao_proposals(Proposals)

    tickets(GitHub Issues)

    contribs -- Develop --> product
    contribs -- Design --> product
    contribs -- Promote --> product
    contribs -- Vote with IVY --> dao_proposals

    product -- Acquire --> users

    users -- Reviews --> product
    users -- Donate crypto --> dao

    dao -- Store Donations --> dao_dev_fund
    dao -- Smart Contract --> dao_proposals
    dao_dev_fund -- Bounty --> dao_proposals

    dao_proposals -- If passed voting --> tickets
    tickets -- Earn: Bounty + IVY --> contribs
  
```