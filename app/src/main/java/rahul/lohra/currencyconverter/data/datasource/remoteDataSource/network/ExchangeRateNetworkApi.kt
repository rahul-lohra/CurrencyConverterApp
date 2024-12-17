package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeRateNetworkApi {
    object Config {

        const val APP_ID = "9fa876d052a44b7cb9f04bd21d03ad1a"
        const val BASE_URL = "https://openexchangerates.org"
    }

    object QueryParams {
        const val BASE = "base"
    }

    object RequestHeaders {
        const val AUTHORIZATION = "Authorization"
        private const val TOKEN = "Token"
        const val AUTHORIZATION_VALUE = "$TOKEN ${Config.APP_ID}"
        const val CACHE_CONTROL_HEADERS = "private, max-age=1800"
    }

    @GET("/api/latest.json")
    suspend fun getCurrencies(
        @Query(QueryParams.BASE) base: String,
    ): CurrencyResponse
}


data class CurrencyResponse(
    @SerializedName("base")
    val base:String,
    @SerializedName("rates")
    val rates:Map<String, Float>)