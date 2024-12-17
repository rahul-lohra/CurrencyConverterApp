package rahul.lohra.currencyconverter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import rahul.lohra.currencyconverter.Constants
import rahul.lohra.currencyconverter.domain.CurrencyExchangeUseCase
import rahul.lohra.currencyconverter.domain.UseCaseResultSuccess
import rahul.lohra.currencyconverter.ui.states.shared.CurrencySelectedFromPickerUiStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.BaseCurrencyStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.states.shared.EnteredAmountSharedState
import rahul.lohra.currencyconverter.ui.states.CurrencyExchangeUiModel
import rahul.lohra.currencyconverter.ui.states.UiFail
import rahul.lohra.currencyconverter.ui.states.UiLoading
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val useCase: CurrencyExchangeUseCase,
    val baseCurrencyStateHolder: BaseCurrencyStateHolder,
    private val currencySelectedFromPickerStateHolder: CurrencySelectedFromPickerUiStateHolder,
    private val currencyExchangeSharedState: CurrencyExchangeSharedState,
    private val enteredAmountSharedState: EnteredAmountSharedState,
    @Named("debounceTime") private val debounceTime: Long,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    companion object {
        const val KEY = "CurrencyExchangeViewModel"
    }

    val currencyExchangeFlow: StateFlow<UiState<CurrencyExchangeUiModel>> =
        currencyExchangeSharedState.currencyExchangeFlow
    val enteredAmountFlow: StateFlow<String> = enteredAmountSharedState.enteredAmountFlow

    init {
        observeBaseCurrency()
        observePickerCurrency()
    }

    fun onAmountChange(amount: String) {
        viewModelScope.launch(ioDispatcher) {
            if (amount.isNotEmpty()) {
                val f = amount.toFloatOrNull()
                if (f != null) {
                    enteredAmountSharedState.updateEnteredAmount(amount)
                }
            } else {
                enteredAmountSharedState.updateEnteredAmount("")
            }
        }
    }


    fun observeBaseCurrency() {
        baseCurrencyStateHolder.sharedState
            .debounce(debounceTime)  // Rate limiter: Wait 500ms after the last change before fetching data
            .distinctUntilChanged()  // Only fetch if the base currency actually changes
            .flatMapLatest { baseCurrency ->
                flow {
                    emit(UiLoading<CurrencyExchangeUiModel>())
                    emit(fetchCurrencyExchangeRates(baseCurrency))
                }.flowOn(ioDispatcher)
            }.onEach { uiState ->
                currencyExchangeSharedState.updateCurrencyExchangeState(uiState)
            }
            .launchIn(viewModelScope)
    }

    fun observePickerCurrency() {
        currencySelectedFromPickerStateHolder.sharedState
            .filter { it != baseCurrencyStateHolder.sharedState.value } // Only proceed if the value has changed
            .onEach { baseCurrencyStateHolder.updateState(it) }
            .flowOn(ioDispatcher)
            .launchIn(viewModelScope)
    }

    suspend fun fetchCurrencyExchangeRates(baseCurrency: String): UiState<CurrencyExchangeUiModel> {
        val result = useCase.getCurrency(baseCurrency)
        return if (result is UseCaseResultSuccess) {
            UiSuccess<CurrencyExchangeUiModel>(
                CurrencyExchangeUiModel(
                    result.data.base,
                    result.data.rates
                )
            )
        } else {
            UiFail<CurrencyExchangeUiModel>(Constants.DEFAULT_ERROR)
        }
    }
}