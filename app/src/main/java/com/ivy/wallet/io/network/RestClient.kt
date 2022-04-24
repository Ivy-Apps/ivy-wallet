package com.ivy.wallet.io.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.google.gson.Gson
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.io.network.error.ErrorCode
import com.ivy.wallet.io.network.error.NetworkError
import com.ivy.wallet.io.network.error.RestError
import com.ivy.wallet.io.network.service.*
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RestClient private constructor(
    private val appContext: Context,
    private val retrofit: Retrofit
) {

    companion object {
        private const val API_URL = "https://ivy-apps.com"
        private const val HEADER_USER_ID = "userId"
        private const val HEADER_SESSION_TOKEN = "sessionToken"

        private var networkAvailable = false

        fun initialize(appContext: Context, session: IvySession, gson: Gson): RestClient {
            val retrofit = newRetrofit(gson, session)
            return RestClient(appContext, retrofit).apply {
                monitorNetworkConnectivity()
            }
        }

        private fun newRetrofit(gson: Gson, session: IvySession): Retrofit {
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


            //Add AUTH headers
            httpClientBuilder.addInterceptor {
                try {
                    val request = it.request()
                        .newBuilder()
                        .addHeader(HEADER_USER_ID, session.getUserId().toString())
                        .addHeader(HEADER_SESSION_TOKEN, session.getSessionToken())
                        .build()

                    it.proceed(request)
                } catch (e: NoSessionException) {
                    //Session not initialized, yet - do nothing
                    it.proceed(it.request())
                }
            }

            //Handle Server errors
            httpClientBuilder.addInterceptor(Interceptor { chain ->
                val response = chain.proceed(chain.request())

                if (response.code < 200 || response.code > 299) {
                    response.body?.string()?.let { errorBody ->
                        try {
                            Timber.e("Server error: $errorBody")
                            val restError = gson.fromJson(
                                errorBody,
                                RestError::class.java
                            ) ?: RestError(ErrorCode.UNKNOWN, "Failed to parse RestError.")
                            throw NetworkError(restError)
                        } catch (exception: Exception) {
                            throw if (exception is NetworkError) exception else {
                                exception.printStackTrace()
                                NetworkError(RestError(ErrorCode.UNKNOWN, exception.message))
                            }
                        }
                    } ?: throw NetworkError(RestError(ErrorCode.UNKNOWN, "Empty error body."))
                }
                response
            })

            //Github Rest API interceptor (not the best solution)
            httpClientBuilder.addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val finalRequest =
                    if (request.url.toUrl().toString().startsWith(GithubService.BASE_URL)) {
                        val credentials = Credentials.basic(
                            GithubService.GITHUB_SERVICE_ACC_USERNAME,
                            GithubService.GITHUB_SERVICE_ACC_ACCESS_TOKEN_PART_1 +
                                    GithubService.GITHUB_SERVICE_ACC_ACCESS_TOKEN_PART_2
                        )

                        request.newBuilder()
                            .header("Authorization", credentials)
                            .build()
                    } else {
                        request
                    }

                chain.proceed(request = finalRequest)
            })

            trustAllSSLCertificates(httpClientBuilder)

            return Retrofit.Builder()
                .baseUrl(API_URL)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        @SuppressLint("TrustAllX509TrustManager")
        fun trustAllSSLCertificates(okHttpBuilder: OkHttpClient.Builder) {
            //TODO: SECURITY - Considering trusting only Ivy's cert
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            okHttpBuilder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            okHttpBuilder.hostnameVerifier(HostnameVerifier { hostname, session ->
                true
            })
        }
    }

    private fun monitorNetworkConnectivity() {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networkAvailable = true
                Timber.d("Network available: $network. (networkAvailable = $networkAvailable)")
            }

            override fun onLost(network: Network) {
                networkAvailable = false
                Timber.d("Network lost: $network. (networkAvailable = $networkAvailable)")
            }

            override fun onUnavailable() {
                networkAvailable = false
                Timber.d("Network unavailable. (networkAvailable = $networkAvailable)")
            }
        })
    }

    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }
    val categoryService: CategoryService by lazy { retrofit.create(CategoryService::class.java) }
    val accountService: AccountService by lazy { retrofit.create(AccountService::class.java) }
    val budgetService: BudgetService by lazy { retrofit.create(BudgetService::class.java) }
    val loanService: LoanService by lazy { retrofit.create(LoanService::class.java) }
    val transactionService: TransactionService by lazy { retrofit.create(TransactionService::class.java) }
    val plannedPaymentRuleService: PlannedPaymentRuleService by lazy {
        retrofit.create(
            PlannedPaymentRuleService::class.java
        )
    }
    val analyticsService: AnalyticsService by lazy { retrofit.create(AnalyticsService::class.java) }
    val coinbaseService: CoinbaseService by lazy { retrofit.create(CoinbaseService::class.java) }
    val githubService: GithubService by lazy { retrofit.create(GithubService::class.java) }
    val nukeService: NukeService by lazy { retrofit.create(NukeService::class.java) }
}