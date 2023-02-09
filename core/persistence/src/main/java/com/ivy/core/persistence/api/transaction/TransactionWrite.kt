package com.ivy.core.persistence.api.transaction

import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionId
import com.ivy.core.persistence.api.Write

interface TransactionWrite : Write<Transaction, TransactionId>