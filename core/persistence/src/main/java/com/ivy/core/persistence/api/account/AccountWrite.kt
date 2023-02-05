package com.ivy.core.persistence.api.account

import com.ivy.core.data.Account
import com.ivy.core.data.AccountId
import com.ivy.core.persistence.api.WriteSyncable

interface AccountWrite : WriteSyncable<Account, AccountId>