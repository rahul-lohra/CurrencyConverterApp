package rahul.lohra.currencyconverter.data.datasource.remoteDataSource

import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResult

interface ExchangeRemoteDataSource {
    suspend fun getCurrenciesApiResult(base: String): ApiResult<CurrencyResponse>
}