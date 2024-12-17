package rahul.lohra.currencyconverter.ui.grid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rahul.lohra.currencyconverter.ui.grid.state.ConvertedRatesGridUIListItem
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.states.shared.EnteredAmountSharedState
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CurrencyGridViewModel @Inject constructor(
    private val currencyExchangeSharedState: CurrencyExchangeSharedState,
    private val enteredAmountSharedFlow: EnteredAmountSharedState,
    @Named("defaultDispatcher") private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    companion object {
        const val KEY = "CurrencyGridViewModel"
    }

    private val _convertedRateUiList =
        MutableStateFlow<UiState<List<ConvertedRatesGridUIListItem>>>(UiInitial())
    val convertedRateUiList: StateFlow<UiState<List<ConvertedRatesGridUIListItem>>> =
        _convertedRateUiList

    init {
        collectConvertedRates()
    }

    fun collectConvertedRates() {
        viewModelScope.launch(defaultDispatcher) {
            enteredAmountSharedFlow.enteredAmountFlow.collectLatest { amount ->
                if (amount.isNotEmpty()) {
                    val enteredAmountInFloat = amount.toFloatOrNull()
                    if (enteredAmountInFloat != null && currencyExchangeSharedState.currencyExchangeFlow.value is UiSuccess) {
                        val ratesMap = (currencyExchangeSharedState.currencyExchangeFlow.value as UiSuccess).data.rates
                        val newRates = ratesMap.mapValues { it.value * enteredAmountInFloat }
                            .map { ConvertedRatesGridUIListItem(it.key, it.value.toString()) }
                        _convertedRateUiList.emit(UiSuccess(newRates))
                    }
                } else {
                    _convertedRateUiList.emit(UiInitial())
                }

            }
        }
    }
}