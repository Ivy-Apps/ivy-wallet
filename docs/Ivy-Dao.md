# Ivy DAO

## High-level picture
```mermaid
graph TD;
    contribs(Contributors)
    users(New Users)
    inf(Influencers)
    dao(Ivy DAO)
    product(Ivy Wallet App)

    dao_marketing_fund(Marketing Fund)
    dao_proposals(Proposals)
    dao_dev_fund(Dev Fund)
    contribs_design(Design)
    contribs_dev(Develop)
    contribs_vote(Vote)
    u_reviews(Reviews)
    u_donate(Donate)
    inf_promote(Promote)

    tickets(GitHub Issues)
    tickets --> contribs
    dao_dev_fund --> tickets
    dao_proposals --> tickets    

    contribs --> contribs_dev
    contribs --> contribs_design
    contribs --> contribs_vote

    contribs_vote --> dao
    contribs_design --> product
    contribs_dev --> product
  
    dao --> dao_proposals
    dao_proposals --> contribs
    dao --> dao_dev_fund
    dao --> dao_marketing_fund --> inf
    dao_dev_fund --> contribs

    product --> users

    users --> u_reviews
    users --> u_donate

    u_reviews --> product
    u_donate --> dao  

    inf --> inf_promote
    inf_promote --> product
```