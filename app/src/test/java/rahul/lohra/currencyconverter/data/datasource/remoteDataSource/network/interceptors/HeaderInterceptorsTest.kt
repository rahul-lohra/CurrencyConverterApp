package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.interceptors

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi

class HeaderInterceptorsTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptors())
            .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun intercept() {
        // Enqueue a mock response
        mockWebServer.enqueue(MockResponse().setBody("{}").setResponseCode(200))

        // Create a request
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()

        // Execute the request
        okHttpClient.newCall(request).execute()

        // Capture the request sent to the mock server
        val recordedRequest = mockWebServer.takeRequest()

        // Verify headers
        assertEquals(OpenExchangeRateNetworkApi.RequestHeaders.AUTHORIZATION_VALUE, recordedRequest.getHeader(OpenExchangeRateNetworkApi.RequestHeaders.AUTHORIZATION))
        assertEquals(OpenExchangeRateNetworkApi.RequestHeaders.CACHE_CONTROL_HEADERS, recordedRequest.getHeader("Cache-Control"))
        assertEquals("application/json", recordedRequest.getHeader("Accept"))
    }
}