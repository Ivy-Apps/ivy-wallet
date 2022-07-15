package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.auth.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/google-sign-in")
    suspend fun googleSignIn(@Body request: GoogleSignInRequest): AuthResponse

    @POST("/auth/initiate-reset-password")
    suspend fun initiateResetPassword(
        @Body request: InitiateResetPasswordRequest
    ): InitiateResetPasswordResponse

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): AuthResponse

    @POST("/auth/update-user-info")
    suspend fun updateUserInfo(@Body request: UpdateUserInfoRequest): UpdateUserInfoResponse

}