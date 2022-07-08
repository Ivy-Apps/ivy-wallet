package com.ivy.wallet.domain.action.viewmodel.experiment

import com.ivy.frp.action.FPAction
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.tryOp
import com.ivy.wallet.io.network.service.ExpImagesService
import javax.inject.Inject

typealias FetchImagesRes = Res<Exception, List<String>>

class FetchImagesAct @Inject constructor(
    private val expImagesService: ExpImagesService
) : FPAction<Unit, FetchImagesRes>() {
    override suspend fun Unit.compose(): suspend () -> FetchImagesRes = tryOp(
        operation = expImagesService::fetchImages
    )
}