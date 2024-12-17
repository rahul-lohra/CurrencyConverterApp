package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import rahul.lohra.currencyconverter.CurrencyConverterApp
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.interceptors.CacheNetworkLoggingInterceptor
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.interceptors.HeaderInterceptors
import rahul.lohra.currencyconverter.isAppInDebug
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object ExchangeRateNetworkClient {

    private fun createOkHttpCache(app: CurrencyConverterApp): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong()
        val cacheDirectory = File(app.cacheDir, "http_cache")
        return Cache(cacheDirectory, cacheSize)
    }

    private fun createOkHttpClient(app: CurrencyConverterApp, isDebug: Boolean): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(createOkHttpCache(app))
            .addInterceptor(CacheNetworkLoggingInterceptor())
            .addInterceptor(HeaderInterceptors())
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (isDebug) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            .build()
    }

    fun provideNetworkClient(
        app: CurrencyConverterApp = CurrencyConverterApp.INSTANCE,
        isDebug: Boolean = app.isAppInDebug()
    ): OpenExchangeRateNetworkApi {

        val client = createOkHttpClient(app, isDebug)
        val retrofit = Retrofit.Builder()
            .baseUrl(OpenExchangeRateNetworkApi.Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OpenExchangeRateNetworkApi::class.java)
    }
}