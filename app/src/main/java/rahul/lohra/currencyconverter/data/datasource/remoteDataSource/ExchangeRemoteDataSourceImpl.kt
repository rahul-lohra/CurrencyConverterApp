package rahul.lohra.currencyconverter.data.datasource.remoteDataSource

import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResult
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultFail
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultSuccess

class ExchangeRemoteDataSourceImpl(private val openExchangeRateNetworkApi: OpenExchangeRateNetworkApi) :
    ExchangeRemoteDataSource {

        override suspend fun getCurrenciesApiResult(base: String): ApiResult<CurrencyResponse> {
        try {
            val apiResult = openExchangeRateNetworkApi.getCurrencies(base)
            return ApiResultSuccess(apiResult)
        } catch (ex: Exception) {
            return ApiResultFail(ex)
        }
    }
}