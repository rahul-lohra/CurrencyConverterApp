package rahul.lohra.currencyconverter.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.ExchangeRemoteDataSource
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResult
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultFail
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultSuccess
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse

class ExchangeRateRepositoryTest {
    private lateinit var repository: ExchangeRateRepository
    private lateinit var remoteDataSource: ExchangeRemoteDataSource

    // Use the TestCoroutineDispatcher for coroutine testing
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    @Before
    fun setUp() {
        remoteDataSource = mockk()
        repository = ExchangeRateRepository(remoteDataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getCurrenciesApiResult returns success result`() = testScope.runTest {
        // Arrange
        val baseCurrency = "USD"
        val mockResponse = CurrencyResponse(
            base = baseCurrency,
            rates = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        )
        val expectedResult = ApiResultSuccess(mockResponse)

        // Set up the mock behavior
        coEvery { remoteDataSource.getCurrenciesApiResult(baseCurrency) } returns expectedResult

        // Act
        val result = repository.getCurrenciesApiResult(baseCurrency)

        // Assert
        assertTrue(result is ApiResultSuccess)
        assertEquals(mockResponse, (result as ApiResultSuccess).data)

        // Verify that the remote data source was called
        coVerify { remoteDataSource.getCurrenciesApiResult(baseCurrency) }
    }

    @Test
    fun `getCurrenciesApiResult returns error result`() = testScope.runTest {
        val baseCurrency = "USD"
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)
        val expectedResult = ApiResultFail<CurrencyResponse>(exception)

        coEvery { remoteDataSource.getCurrenciesApiResult(baseCurrency) } returns expectedResult

        val result = repository.getCurrenciesApiResult(baseCurrency)

        assertTrue(result is ApiResultFail)
        assertEquals(errorMessage, (result as ApiResultFail).ex.message)

        coVerify { remoteDataSource.getCurrenciesApiResult(baseCurrency) }
    }

}