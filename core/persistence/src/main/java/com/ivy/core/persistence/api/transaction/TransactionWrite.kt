package com.ivy.core.persistence.api.transaction

import com.ivy.core.data.TransactionId
import com.ivy.core.persistence.api.WriteSyncable
import com.ivy.data.transaction.Transaction

interface TransactionWrite : WriteSyncable<Transaction, TransactionId>