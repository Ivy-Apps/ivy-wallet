package com.ivy.wallet.ui.serverstop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.domain.deprecated.logic.zip.BackupLogic
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerStopViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val sharedPrefs: SharedPrefs,
    private val backupLogic: BackupLogic,
    private val navigation: Navigation,
) : ViewModel() {
    private val exportInProgress = MutableStateFlow(false)

    val state = exportInProgress.map { exportInProgress ->
        ServerStopState(
            exportInProgress = exportInProgress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ServerStopState(
            exportInProgress = false
        )
    )

    fun exportToZip(context: Context) {
        ivyContext.createNewFile(
            "Ivy-Wallet-backup.zip"
        ) { fileUri ->
            viewModelScope.launch(Dispatchers.IO) {
                TestIdlingResource.increment()

                exportInProgress.value = true
                backupLogic.exportToFile(zipFileUri = fileUri)
                exportInProgress.value = false

                sharedPrefs.putBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, true)
                ivyContext.dataBackupCompleted = true

                uiThread {
                    (context as RootActivity).shareZipFile(
                        fileUri = fileUri
                    )
                    navigation.back()
                }

                TestIdlingResource.decrement()
            }
        }
    }
}
