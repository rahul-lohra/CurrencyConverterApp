package rahul.lohra.currencyconverter.ui.grid.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import rahul.lohra.currencyconverter.ui.grid.state.ConvertedRatesGridUIListItem
import rahul.lohra.currencyconverter.ui.states.CurrencyExchangeUiModel
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.states.shared.EnteredAmountSharedState

class CurrencyGridViewModelTest {
    private lateinit var currencyExchangeSharedState: CurrencyExchangeSharedState
    private lateinit var enteredAmountSharedState: EnteredAmountSharedState
    private lateinit var viewModel: CurrencyGridViewModel

    // Use a test dispatcher
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Initialize flows
        currencyExchangeSharedState = CurrencyExchangeSharedState()
        enteredAmountSharedState = EnteredAmountSharedState()

        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel with test dispatcher
        viewModel = CurrencyGridViewModel(
            currencyExchangeSharedState,
            enteredAmountSharedState,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when amount and exchange rates are provided, converted rates are emitted correctly`() =
        runTest {
            // Arrange
            val ratesMap = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
            val baseCurrency = "USD"
            val exchangeUiModel = CurrencyExchangeUiModel(baseCurrency, ratesMap)
            currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

            // Collect the outputs
            val collectedOutputs = mutableListOf<UiState<List<ConvertedRatesGridUIListItem>>>()

            val job = launch {
                viewModel.convertedRateUiList.collect {
                    collectedOutputs.add(it)
                }
            }

            // Act
            enteredAmountSharedState.updateEnteredAmount("100")

            // Advance time if necessary
            advanceUntilIdle()

            // Assert
            val expectedRates = listOf(
                ConvertedRatesGridUIListItem("EUR", "85.0"),
                ConvertedRatesGridUIListItem("JPY", "11000.0")
            )

            val lastEmittedState = collectedOutputs.last()

            assertTrue(lastEmittedState is UiSuccess)
            val uiSuccess = lastEmittedState as UiSuccess<List<ConvertedRatesGridUIListItem>>
            assertEquals(expectedRates, uiSuccess.data)

            job.cancel()
        }

    @Test
    fun `when amount is empty, convertedRateUiList emits UiInitial`() = runTest {
        // Arrange
        val ratesMap = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "USD"
        val exchangeUiModel = CurrencyExchangeUiModel(baseCurrency, ratesMap)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Collect the outputs
        val collectedOutputs = mutableListOf<UiState<List<ConvertedRatesGridUIListItem>>>()

        val job = launch {
            viewModel.convertedRateUiList.collect {
                collectedOutputs.add(it)
            }
        }

        // Act
        enteredAmountSharedState.updateEnteredAmount("")

        advanceUntilIdle()

        // Assert
        val lastEmittedState = collectedOutputs.last()

        assertTrue(lastEmittedState is UiInitial)

        job.cancel()
    }

    @Test
    fun `when exchangeRatesFlowWithUiState is not UiSuccess, convertedRateUiList remains UiInitial`() =
        runTest {
            // Arrange
            currencyExchangeSharedState.updateCurrencyExchangeState(UiInitial())

            // Collect the outputs
            val collectedOutputs = mutableListOf<UiState<List<ConvertedRatesGridUIListItem>>>()

            val job = launch {
                viewModel.convertedRateUiList.collect {
                    collectedOutputs.add(it)
                }
            }

            // Act
            enteredAmountSharedState.updateEnteredAmount("100")

            advanceUntilIdle()

            // Assert
            val lastEmittedState = collectedOutputs.last()

            assertTrue(lastEmittedState is UiInitial)

            job.cancel()
        }

    @Test
    fun `when amount is invalid, convertedRateUiList remains unchanged`() = runTest {
        // Arrange
        val ratesMap = mapOf("EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "USD"
        val exchangeUiModel = CurrencyExchangeUiModel(baseCurrency, ratesMap)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Collect the outputs
        val collectedOutputs = mutableListOf<UiState<List<ConvertedRatesGridUIListItem>>>()

        val job = launch {
            viewModel.convertedRateUiList.collect {
                collectedOutputs.add(it)
            }
        }

        // Act
        enteredAmountSharedState.updateEnteredAmount("abc")

        advanceUntilIdle()

        // Assert
        val lastEmittedState = collectedOutputs.last()

        // Since the ViewModel doesn't handle invalid amount exceptions, the coroutine might crash.
        // For a robust test, we need to modify the ViewModel to handle exceptions.
        // But based on the current code, the last emitted state should still be UiInitial.
        assertTrue(lastEmittedState is UiInitial)

        job.cancel()
    }
}