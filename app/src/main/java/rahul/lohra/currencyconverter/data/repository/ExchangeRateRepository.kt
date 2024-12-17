package rahul.lohra.currencyconverter.data.repository

import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.ExchangeRemoteDataSource
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResult

class ExchangeRateRepository(
    private val exchangeRemoteDataSource: ExchangeRemoteDataSource) {

    suspend fun getCurrenciesApiResult(base: String): ApiResult<CurrencyResponse> {
        return exchangeRemoteDataSource.getCurrenciesApiResult(base)
    }
}