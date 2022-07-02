package com.ivy.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RestClient private constructor(
    private val appContext: Context,
    private val retrofit: Retrofit
) {
    fun <T> create(service: Class<T>): T = retrofit.create(service)

    companion object {
        private var networkAvailable = false

        fun initialize(
            appContext: Context,
            gson: Gson,
            baseUrl: String,
            headers: () -> List<Header>,
            errorHandling: (Int, String?) -> IOException
        ): RestClient {
            val retrofit = newRetrofit(
                gson = gson,
                baseUrl = baseUrl,
                headers = headers,
                errorHandling = errorHandling
            )
            return RestClient(
                appContext = appContext,
                retrofit = retrofit
            ).apply {
                monitorNetworkConnectivity()
            }
        }

        private fun newRetrofit(
            gson: Gson,
            baseUrl: String,
            headers: () -> List<Header>,
            errorHandling: (Int, String?) -> IOException
        ): Retrofit {
            val httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .callTimeout(15, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }

                httpClientBuilder.addInterceptor(loggingInterceptor)
            }

            //Add headers
            httpClientBuilder.addInterceptor {
                val requestBuilder = it.request()
                    .newBuilder()

                headers().forEach { header ->
                    requestBuilder.addHeader(header.name, header.value)
                }
                val request = requestBuilder.build()

                it.proceed(request)
            }

            //Handle errors
            httpClientBuilder.addInterceptor(Interceptor { chain ->
                val response = chain.proceed(chain.request())

                if (response.code < 200 || response.code > 299) {
                    val errorBody = response.body?.string()
                    errorHandling(response.code, errorBody)
                }
                response
            })

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    private fun monitorNetworkConnectivity() {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networkAvailable = true
                Timber.d("Network available: $network.")
            }

            override fun onLost(network: Network) {
                networkAvailable = false
                Timber.d("Network lost: $network.")
            }

            override fun onUnavailable() {
                networkAvailable = false
                Timber.d("Network unavailable.")
            }
        })
    }
}