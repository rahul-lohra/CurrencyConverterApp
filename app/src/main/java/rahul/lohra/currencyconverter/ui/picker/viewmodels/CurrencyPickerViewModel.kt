package rahul.lohra.currencyconverter.ui.picker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rahul.lohra.currencyconverter.ui.states.shared.CurrencySelectedFromPickerUiStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.BaseCurrencyStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.picker.state.CurrencyListItem
import rahul.lohra.currencyconverter.ui.states.CurrencyExchangeUiModel
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CurrencyPickerViewModel @Inject constructor(
    private val currencyExchangeSharedState: CurrencyExchangeSharedState,
    private val currencyStateHolder: BaseCurrencyStateHolder,
    private val currencySelectedFromPickerUiStateHolder: CurrencySelectedFromPickerUiStateHolder,
    @Named("defaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
): ViewModel() {
    companion object {
        const val KEY = "CurrencyPickerViewModel"
    }
    private val _currencyPickerUiList =
        MutableStateFlow<UiState<List<CurrencyListItem>>>(UiInitial())
    val currencyPickerUiList: StateFlow<UiState<List<CurrencyListItem>>> = _currencyPickerUiList

    init {
        collectExchangeCurrency()
    }

    private fun collectExchangeCurrency() {
        viewModelScope.launch(defaultDispatcher) {
            currencyExchangeSharedState.currencyExchangeFlow.collectLatest { state ->
                if (state is UiSuccess) {
                    val selectedBaseCurrency = currencyStateHolder.sharedState.value
                    val messages =
                        (state as UiSuccess<CurrencyExchangeUiModel>).data.rates.map {
                            CurrencyListItem(it.key, it.value, it.key == selectedBaseCurrency)
                        }
                    _currencyPickerUiList.emit(UiSuccess(messages))
                }

            }
        }
    }

    fun selectCurrencyFromPickerList(text: String) {
        viewModelScope.launch(defaultDispatcher) {
            val state = currencyExchangeSharedState.currencyExchangeFlow.value
            if (state is UiSuccess) {
                val messages =
                    (state as UiSuccess<CurrencyExchangeUiModel>).data.rates.map {
                        CurrencyListItem(it.key, it.value, text == it.key)
                    }
                _currencyPickerUiList.emit(UiSuccess(messages))
            }
        }
    }

    fun confirmSelectedCurrency() {
        viewModelScope.launch(defaultDispatcher) {
            val state = currencyPickerUiList.value
            if (state is UiSuccess) {
                val selectedCurrency = state.data.firstOrNull { it.isSelected }
                if (selectedCurrency != null) {
                    currencySelectedFromPickerUiStateHolder.updateState(selectedCurrency.countryCode)
                }
            }
        }
    }
}