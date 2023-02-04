package com.ivy.core.persistence.api.recurring

import com.ivy.core.data.TransactionId
import com.ivy.core.persistence.api.Write
import com.ivy.data.transaction.Transaction

interface DueTransactionWrite : Write<Transaction, TransactionId>