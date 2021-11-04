package com.ivy.wallet.network.error

import java.io.IOException

class NetworkError(val restError: RestError) :
    IOException("Network error: ${restError.errorCode.code} - ${restError.msg}")