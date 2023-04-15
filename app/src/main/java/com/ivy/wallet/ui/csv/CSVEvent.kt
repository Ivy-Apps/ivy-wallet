package com.ivy.wallet.ui.csv

import android.net.Uri

sealed interface CSVEvent {
    data class FilePicked(val uri: Uri) : CSVEvent

}