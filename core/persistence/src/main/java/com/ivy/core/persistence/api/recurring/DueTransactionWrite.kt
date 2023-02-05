package com.ivy.core.persistence.api.recurring

import com.ivy.core.data.TransactionId
import com.ivy.core.persistence.api.WriteSyncable
import com.ivy.data.transaction.Transaction

interface DueTransactionWrite : WriteSyncable<Transaction, TransactionId>