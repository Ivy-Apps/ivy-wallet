package com.ivy.wallet.io.network.error

import java.io.IOException

class NetworkError(val restError: RestError) :
    IOException("Network error: ${restError.errorCode.code} - ${restError.msg}")