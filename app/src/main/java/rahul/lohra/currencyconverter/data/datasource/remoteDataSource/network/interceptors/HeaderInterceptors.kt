package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi

class HeaderInterceptors : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header(
                OpenExchangeRateNetworkApi.RequestHeaders.AUTHORIZATION,
                OpenExchangeRateNetworkApi.RequestHeaders.AUTHORIZATION_VALUE
            )
            .header(
                "Cache-Control",
                OpenExchangeRateNetworkApi.RequestHeaders.CACHE_CONTROL_HEADERS
            )
            .header("Accept", "application/json")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}