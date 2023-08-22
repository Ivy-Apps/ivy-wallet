package com.ivy.wallet.io.network.service

import androidx.annotation.Keep
import retrofit2.http.GET

@Keep
interface ExpImagesService {
    @GET
    suspend fun fetchImages(): List<String>
}