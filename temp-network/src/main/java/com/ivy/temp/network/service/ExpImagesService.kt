package com.ivy.wallet.io.network.service

import retrofit2.http.GET

interface ExpImagesService {
    @GET
    suspend fun fetchImages(): List<String>
}