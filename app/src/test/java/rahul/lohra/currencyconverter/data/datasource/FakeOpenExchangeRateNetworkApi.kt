package rahul.lohra.currencyconverter.data.datasource

import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi

class FakeOpenExchangeRateNetworkApi: OpenExchangeRateNetworkApi {
    var sendError = false
    override suspend fun getCurrencies(base: String): CurrencyResponse {
        if(sendError){
            throw Exception("Exception")
        }
        return CurrencyResponse(base, mapOf("USD" to 1f, "INR" to 70f))
    }
}