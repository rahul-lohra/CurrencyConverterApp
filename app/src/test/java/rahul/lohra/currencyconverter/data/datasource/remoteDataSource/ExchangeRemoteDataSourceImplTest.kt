package rahul.lohra.currencyconverter.data.datasource.remoteDataSource

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rahul.lohra.currencyconverter.data.datasource.FakeOpenExchangeRateNetworkApi
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultFail
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data.ApiResultSuccess


class ExchangeRemoteDataSourceImplTest {
    private lateinit var fakeOpenExchangeRateNetworkApi: FakeOpenExchangeRateNetworkApi
    private lateinit var openExchangeRemoteDataSourceImpl: ExchangeRemoteDataSourceImpl

    @Before
    fun setup() {
        fakeOpenExchangeRateNetworkApi = FakeOpenExchangeRateNetworkApi()
        openExchangeRemoteDataSourceImpl =
            ExchangeRemoteDataSourceImpl(fakeOpenExchangeRateNetworkApi)
    }

    @Test
    fun `test success result`() {
        runTest {
            val base = ""
            val result = openExchangeRemoteDataSourceImpl.getCurrenciesApiResult(base)
            assertTrue(result is ApiResultSuccess)
        }
    }

    @Test
    fun `test fail result`() {
        runTest {
            val base = ""
            fakeOpenExchangeRateNetworkApi.sendError = true
            val result = openExchangeRemoteDataSourceImpl.getCurrenciesApiResult(base)
            assertTrue(result is ApiResultFail)
        }
    }
}