package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import rahul.lohra.currencyconverter.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OpenExchangeRateNetworkApiTest {
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenExchangeRateNetworkApi::class.java)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should fetch open exchange rates correctly given 200 response`() {
        runTest {
            val mockResponseText = readJsonFromResource("api_responses/success_api_response.json")

            val response = MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseText)
            mockWebServer.enqueue(response)

            val actual = api.getCurrencies(Constants.DEFAULT_CURRENCY)
            val expected = CurrencyResponse(Constants.DEFAULT_CURRENCY, mapOf("AED" to 3.673f,
                "AFN" to 68.109248f,
            "ALL" to 88.291087f))
            assertEquals(expected, actual)
        }
    }

    fun readJsonFromResource(fileName: String): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader?.getResourceAsStream(fileName)
        return inputStream?.bufferedReader().use { it?.readText() }
            ?: throw IllegalArgumentException("File not found")
    }
}