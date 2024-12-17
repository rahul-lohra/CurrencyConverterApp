package rahul.lohra.currencyconverter.domain

import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rahul.lohra.currencyconverter.Constants
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultFail
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultSuccess
import rahul.lohra.currencyconverter.data.repository.ExchangeRateRepository
import kotlin.math.pow
import kotlin.math.round

class CurrencyExchangeUseCaseTest {
    lateinit var useCase: CurrencyExchangeUseCase
    lateinit var repository: ExchangeRateRepository

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CurrencyExchangeUseCase(repository)
    }

    @Test
    fun `getCurrency should return UseCaseResultSuccess when repository returns ApiResultSuccess`() {
        runTest {
            //Given
            val base = Constants.DEFAULT_CURRENCY
            val response = CurrencyResponse(base, mapOf("USD" to 1.2f, "INR" to 88.0f))
            coEvery { repository.getCurrenciesApiResult(base) } returns ApiResultSuccess(response)

            //When
            val result = useCase.getCurrency(base)

            //Then
            assert(result is UseCaseResultSuccess)
            assertEquals((result as UseCaseResultSuccess).data, response)
            coVerify { repository.getCurrenciesApiResult(base) }
        }
    }

    @Test
    fun `getCurrency should return UseCaseResultSuccess with converted rates when fallback to BASE_CURRENCY is successful`() = runTest {
        // Given
        val base = "EUR"
        val failResponse = Exception("Network error")
        val baseCurrencyResponse = CurrencyResponse("USD", hashMapOf("USD" to 1.0f, "EUR" to 0.85f, "INR" to 88.0f))

        coEvery { repository.getCurrenciesApiResult(base) } returns ApiResultFail(failResponse)
        coEvery { repository.getCurrenciesApiResult(Constants.DEFAULT_CURRENCY) } returns ApiResultSuccess(baseCurrencyResponse)

        // When
        val result = useCase.getCurrency(base)

        // Then
        assert(result is UseCaseResultSuccess)
        val expectedRates = hashMapOf("USD" to 1.18f, "EUR" to 1.0f, "INR" to 103.53f) // Values divided by the rate of EUR (0.85)
        assertEquals((result as UseCaseResultSuccess).data.base, base)
        val rateFromUseCase = HashMap(result.data.rates.mapValues { it.value.roundTo(2) })
        assertEquals(rateFromUseCase, expectedRates)

        coVerify {
            repository.getCurrenciesApiResult(base)
            repository.getCurrenciesApiResult(Constants.DEFAULT_CURRENCY)
        }
    }

    private fun Float.roundTo(decimals: Int): Float {
        val factor = 10.0.pow(decimals).toFloat()
        return round(this * factor) / factor
    }

    @Test
    fun `getCurrency should return UseCaseResultFailure when both initial and fallback API calls fail`() = runTest {
        // Given
        val base = "EUR"
        val exception = Exception("Network error")

        coEvery { repository.getCurrenciesApiResult(base) } returns ApiResultFail(exception)
        coEvery { repository.getCurrenciesApiResult(Constants.DEFAULT_CURRENCY) } returns ApiResultFail(exception)

        // When
        val result = useCase.getCurrency(base)

        // Then
        assert(result is UseCaseResultFailure)
        assertEquals((result as UseCaseResultFailure).exception, exception)

        coVerify {
            repository.getCurrenciesApiResult(base)
            repository.getCurrenciesApiResult(Constants.DEFAULT_CURRENCY)
        }
    }

    @After
    fun tearDown() {
        clearMocks(repository)
    }
}