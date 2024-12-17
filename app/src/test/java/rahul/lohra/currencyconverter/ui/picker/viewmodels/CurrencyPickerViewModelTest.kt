package rahul.lohra.currencyconverter.ui.picker.viewmodels

import kotlinx.coroutines.Dispatchers
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
import rahul.lohra.currencyconverter.ui.states.shared.CurrencySelectedFromPickerUiStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.BaseCurrencyStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.picker.state.CurrencyListItem
import rahul.lohra.currencyconverter.ui.states.CurrencyExchangeUiModel
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess

class CurrencyPickerViewModelTest {
    private lateinit var currencyExchangeSharedState: CurrencyExchangeSharedState
    private lateinit var currencyStateHolder: BaseCurrencyStateHolder
    private lateinit var currencySelectedStateHolder: CurrencySelectedFromPickerUiStateHolder
    private lateinit var viewModel: CurrencyPickerViewModel

    // Use a test dispatcher
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Initialize flows and state holders
        currencyExchangeSharedState = CurrencyExchangeSharedState()
        currencyStateHolder = BaseCurrencyStateHolder()
        currencySelectedStateHolder = CurrencySelectedFromPickerUiStateHolder()

        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel
        viewModel = CurrencyPickerViewModel(
            currencyExchangeSharedState,
            currencyStateHolder,
            currencySelectedStateHolder,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when currencyExchangeFlow emits UiSuccess, currencyPickerUiList emits correct data`() = runTest {
        // Arrange
        val rates = mapOf("USD" to 1.0f, "EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "EUR"
        currencyStateHolder.updateState(baseCurrency)

        // Collect outputs from currencyPickerUiList
        val collectedOutputs = mutableListOf<UiState<List<CurrencyListItem>>>()
        val job = launch {
            viewModel.currencyPickerUiList.collect {
                collectedOutputs.add(it)
            }
        }

        // Act
        val exchangeUiModel = CurrencyExchangeUiModel("USD",rates)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Advance time
        advanceUntilIdle()

        // Assert
        val lastState = collectedOutputs.last()
        assertTrue(lastState is UiSuccess)
        val uiSuccess = lastState as UiSuccess<List<CurrencyListItem>>

        val expectedItems = rates.map { (key, value) ->
            CurrencyListItem(
                countryCode = key,
                rate = value,
                isSelected = key == baseCurrency
            )
        }.sortedBy { it.countryCode }

        val actualItems = uiSuccess.data.sortedBy { it.countryCode }
        assertEquals(expectedItems, actualItems)

        job.cancel()
    }

    @Test
    fun `when selectCurrencyFromPickerList is called, currencyPickerUiList updates isSelected`() = runTest {
        // Arrange
        val rates = mapOf("USD" to 1.0f, "EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "USD"
        currencyStateHolder.updateState(baseCurrency)

        val exchangeUiModel = CurrencyExchangeUiModel("USD", rates)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Collect outputs from currencyPickerUiList
        val collectedOutputs = mutableListOf<UiState<List<CurrencyListItem>>>()
        val job = launch {
            viewModel.currencyPickerUiList.collect {
                collectedOutputs.add(it)
            }
        }

        // Ensure initial collection
        advanceUntilIdle()

        // Act
        val selectedCurrency = "JPY"
        viewModel.selectCurrencyFromPickerList(selectedCurrency)
        advanceUntilIdle()

        // Assert
        val lastState = collectedOutputs.last()
        assertTrue(lastState is UiSuccess)
        val uiSuccess = lastState as UiSuccess<List<CurrencyListItem>>

        val expectedItems = rates.map { (key, value) ->
            CurrencyListItem(
                countryCode = key,
                rate = value,
                isSelected = key == selectedCurrency
            )
        }.sortedBy { it.countryCode }

        val actualItems = uiSuccess.data.sortedBy { it.countryCode }
        assertEquals(expectedItems, actualItems)

        job.cancel()
    }

    @Test
    fun `when confirmSelectedCurrency is called, currencySelectedStateHolder is updated`() = runTest {
        // Arrange
        val rates = mapOf("USD" to 1.0f, "EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "USD"
        currencyStateHolder.updateState(baseCurrency)

        val exchangeUiModel = CurrencyExchangeUiModel("USD", rates)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Ensure initial collection
        advanceUntilIdle()

        // Update the picker state to select "EUR"
        val selectedCurrency = "EUR"
        viewModel.selectCurrencyFromPickerList(selectedCurrency)
        advanceUntilIdle()

        // Act
        viewModel.confirmSelectedCurrency()
        advanceUntilIdle()

        // Assert
        assertEquals(selectedCurrency, currencySelectedStateHolder.sharedState.value)
    }

    @Test
    fun `when currencyExchangeFlow emits UiInitial, currencyPickerUiList remains UiInitial`() = runTest {
        // Arrange
        val collectedOutputs = mutableListOf<UiState<List<CurrencyListItem>>>()
        val job = launch {
            viewModel.currencyPickerUiList.collect {
                collectedOutputs.add(it)
            }
        }

        // Act
        currencyExchangeSharedState.updateCurrencyExchangeState(UiInitial())
        advanceUntilIdle()

        // Assert
        val lastState = collectedOutputs.last()
        assertTrue(lastState is UiInitial)

        job.cancel()
    }

    @Test
    fun `when no currency is selected, confirmSelectedCurrency does not update currencySelectedStateHolder`() = runTest {
        // Arrange
        val initialSelectedCurrency = "GBP"
        currencySelectedStateHolder.updateState(initialSelectedCurrency)

        val rates = mapOf("USD" to 1.0f, "EUR" to 0.85f, "JPY" to 110.0f)
        val baseCurrency = "AUD" // Not in rates
        currencyStateHolder.updateState(baseCurrency)

        val exchangeUiModel = CurrencyExchangeUiModel("USD",rates)
        currencyExchangeSharedState.updateCurrencyExchangeState(UiSuccess(exchangeUiModel))

        // Ensure initial collection
        advanceUntilIdle()

        // Act
        viewModel.confirmSelectedCurrency()
        advanceUntilIdle()

        // Assert
        assertEquals(initialSelectedCurrency, currencySelectedStateHolder.sharedState.value)
    }

}