package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import rahul.lohra.currencyconverter.CurrencyConverterApp
import rahul.lohra.currencyconverter.isAppInDebug

class CacheNetworkLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (CurrencyConverterApp.INSTANCE.isAppInDebug()) {
            if (response.cacheResponse != null) {
                Log.d("OkHttp", "Response served from cache")
            } else if (response.networkResponse != null) {
                Log.d("OkHttp", "Response served from network")
            }
        }
        return response
    }
}