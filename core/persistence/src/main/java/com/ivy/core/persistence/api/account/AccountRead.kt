package com.ivy.core.persistence.api.account

import com.ivy.core.data.Account
import com.ivy.core.data.AccountId
import com.ivy.core.persistence.api.ReadSyncable

interface AccountRead : ReadSyncable<Account, AccountId, AccountQuery> {

}

sealed interface AccountQuery {
    object All : AccountQuery
}