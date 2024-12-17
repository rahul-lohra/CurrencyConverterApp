package rahul.lohra.currencyconverter.domain

import rahul.lohra.currencyconverter.Constants
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultFail
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultSuccess

import rahul.lohra.currencyconverter.data.repository.ExchangeRateRepository


class CurrencyExchangeUseCase(private val repository: ExchangeRateRepository) {

    suspend fun getCurrency(base: String): UseCaseResult<CurrencyResponse> {
        val result = repository.getCurrenciesApiResult(base)
        return when (result) {
            is ApiResultSuccess -> UseCaseResultSuccess(
                CurrencyResponse(
                    result.data.base,
                    result.data.rates
                )
            )

            is ApiResultFail -> {
                if (base != Constants.DEFAULT_CURRENCY) {
                    val baseResult = repository.getCurrenciesApiResult(Constants.DEFAULT_CURRENCY)
                    if (baseResult is ApiResultSuccess) {
                        val rate = baseResult.data.rates[base]
                        if (rate != null) {
                            val newRates = baseResult.data.rates.mapValues { it.value / rate }
                            return UseCaseResultSuccess(CurrencyResponse(base, newRates))
                        }
                    }
                }
                return UseCaseResultFailure(result.ex)
            }
        }
    }
}