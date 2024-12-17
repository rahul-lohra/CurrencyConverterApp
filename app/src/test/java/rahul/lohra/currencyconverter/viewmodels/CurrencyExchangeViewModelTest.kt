// CurrencyExchangeViewModelTest.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import io.mockk.*
import rahul.lohra.currencyconverter.Constants
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.CurrencyResponse
import rahul.lohra.currencyconverter.domain.CurrencyExchangeUseCase
import rahul.lohra.currencyconverter.domain.UseCaseResultFailure
import rahul.lohra.currencyconverter.domain.UseCaseResultSuccess
import rahul.lohra.currencyconverter.ui.states.shared.CurrencySelectedFromPickerUiStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.BaseCurrencyStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.states.*
import rahul.lohra.currencyconverter.ui.states.shared.EnteredAmountSharedState
import rahul.lohra.currencyconverter.viewmodels.CurrencyExchangeViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyExchangeViewModelTest {

    // Mock dependencies
    private lateinit var useCase: CurrencyExchangeUseCase
    private lateinit var currencyExchangeSharedState: CurrencyExchangeSharedState
    private lateinit var currencyStateHolder: BaseCurrencyStateHolder
    private lateinit var currencySelectedFromPickerStateHolder: CurrencySelectedFromPickerUiStateHolder
    private lateinit var enteredAmountSharedState: EnteredAmountSharedState

    // Use a test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    // ViewModel instance
    private lateinit var viewModel: CurrencyExchangeViewModel

    // Set debounce time to zero for testing
    private val debounceTime = 0L

    @Before
    fun setUp() {
        // Initialize mocks
        useCase = mockk()
        currencyStateHolder = BaseCurrencyStateHolder()
        currencyExchangeSharedState= CurrencyExchangeSharedState()
        currencySelectedFromPickerStateHolder = CurrencySelectedFromPickerUiStateHolder()
        enteredAmountSharedState = EnteredAmountSharedState()

        // Set the main dispatcher to testDispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel with test dispatcher
        viewModel = CurrencyExchangeViewModel(
            useCase,
            currencyStateHolder,
            currencySelectedFromPickerStateHolder,
            currencyExchangeSharedState,
            enteredAmountSharedState,
            debounceTime,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()

        // Clear mocks
        unmockkAll()
    }

    private fun mockUseCaseGetCurrency(baseCurrency: String = "USD") {
//        val baseCurrency = "USD"
        val rates = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        val response = CurrencyResponse(baseCurrency, rates)
        val useCaseResult = UseCaseResultSuccess(response)

        coEvery { useCase.getCurrency(baseCurrency) } returns useCaseResult

    }

    @Test
    fun `onAmountChange with valid amount updates enteredAmountFlow`() = runTest {
        val amount = "123.45"

        mockUseCaseGetCurrency()

        viewModel.onAmountChange(amount)

        // Advance the dispatcher to execute pending coroutines
        advanceUntilIdle()

        Assert.assertEquals(amount, viewModel.enteredAmountFlow.value)
    }

    @Test
    fun `onAmountChange with invalid amount does not update enteredAmountFlow`() = runTest {
        val amount = "abc"

        mockUseCaseGetCurrency()
        viewModel.onAmountChange(amount)

        advanceUntilIdle()

        Assert.assertEquals("", viewModel.enteredAmountFlow.value)
    }

    @Test
    fun `onAmountChange with empty string updates enteredAmountFlow to empty`() = runTest {
        val amount = ""

        mockUseCaseGetCurrency()
        viewModel.onAmountChange(amount)

        advanceUntilIdle()

        Assert.assertEquals("", viewModel.enteredAmountFlow.value)
    }

    @Test
    fun `fetchCurrencyExchangeRates returns UiSuccess when useCase returns success`() = runTest {
        val baseCurrency = "USD"
        val rates = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        val response = CurrencyResponse(baseCurrency, rates)
        val useCaseResult = UseCaseResultSuccess(response)

        coEvery { useCase.getCurrency(baseCurrency) } returns useCaseResult

        val result = viewModel.fetchCurrencyExchangeRates(baseCurrency)

        Assert.assertTrue(result is UiSuccess<CurrencyExchangeUiModel>)
        val uiSuccess = result as UiSuccess<CurrencyExchangeUiModel>
        Assert.assertEquals(baseCurrency, uiSuccess.data.code)
        Assert.assertEquals(rates, uiSuccess.data.rates)
    }

    @Test
    fun `fetchCurrencyExchangeRates returns UiFail when useCase returns failure`() = runTest {
        val baseCurrency = "USD"

        // Mock a failure result
        val useCaseResult = mockk<UseCaseResultFailure>()

        coEvery { useCase.getCurrency(baseCurrency) } returns useCaseResult

        val result = viewModel.fetchCurrencyExchangeRates(baseCurrency)

        Assert.assertTrue(result is UiFail<CurrencyExchangeUiModel>)
        val uiFail = result as UiFail<CurrencyExchangeUiModel>
        Assert.assertEquals(Constants.DEFAULT_ERROR, uiFail.message)
    }

    @Test
    fun `observeBaseCurrency emits UiLoading and UiSuccess when baseCurrency changes`() = runTest {
        val baseCurrency = "USD"
        val rates = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        val response = CurrencyResponse(baseCurrency, rates)
        val useCaseResult = UseCaseResultSuccess(response)

        coEvery { useCase.getCurrency(baseCurrency) } returns useCaseResult

        // Collect the currencyExchangeFlow
        val uiStates = mutableListOf<UiState<CurrencyExchangeUiModel>>()
        val job = launch {
            viewModel.currencyExchangeFlow.collect {
                uiStates.add(it)
            }
        }

        // Update the currencyStateHolder to emit new base currency
        currencyStateHolder.updateState(baseCurrency)

        // Advance the dispatcher
        advanceUntilIdle()

        // Check that UiLoading and UiSuccess are emitted
        Assert.assertEquals(2, uiStates.size)
        Assert.assertTrue(uiStates[0] is UiLoading<CurrencyExchangeUiModel>)
        Assert.assertTrue(uiStates[1] is UiSuccess<CurrencyExchangeUiModel>)

        val uiSuccess = uiStates[1] as UiSuccess<CurrencyExchangeUiModel>
        Assert.assertEquals(baseCurrency, uiSuccess.data.code)
        Assert.assertEquals(rates, uiSuccess.data.rates)

        job.cancel()
    }

    @Test
    fun `observePickerCurrency updates currencyStateHolder when selected currency changes`() = runTest {
        val initialCurrency = "USD"
        val newCurrency = "EUR"

        mockUseCaseGetCurrency(initialCurrency)
        mockUseCaseGetCurrency(newCurrency)

        // Set initial value in currencyStateHolder
        currencyStateHolder.updateState(initialCurrency)

        // Now update currencySelectedFromPickerStateHolder with new currency
        currencySelectedFromPickerStateHolder.updateState(newCurrency)

        // Advance the dispatcher
        advanceUntilIdle()

        // Check that currencyStateHolder sharedState is updated
        Assert.assertEquals(newCurrency, currencyStateHolder.sharedState.value)
    }

    @Test
    fun `observePickerCurrency does not update currencyStateHolder when selected currency is same`() = runTest {
        val currency = "USD"

        mockUseCaseGetCurrency()

        // Set initial value in currencyStateHolder
        currencyStateHolder.updateState(currency)

        // Now update currencySelectedFromPickerStateHolder with same currency
        currencySelectedFromPickerStateHolder.updateState(currency)

        // Advance the dispatcher
        advanceUntilIdle()

        // Check that currencyStateHolder sharedState remains the same
        Assert.assertEquals(currency, currencyStateHolder.sharedState.value)
    }
}