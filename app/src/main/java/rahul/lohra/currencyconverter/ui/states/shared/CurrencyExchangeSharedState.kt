package rahul.lohra.currencyconverter.ui.states.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rahul.lohra.currencyconverter.ui.states.CurrencyExchangeUiModel
import rahul.lohra.currencyconverter.ui.states.UiLoading
import rahul.lohra.currencyconverter.ui.states.UiState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyExchangeSharedState @Inject constructor() {
    private val _currencyExchangeFlow = MutableStateFlow<UiState<CurrencyExchangeUiModel>>(UiLoading())
    val currencyExchangeFlow: StateFlow<UiState<CurrencyExchangeUiModel>> = _currencyExchangeFlow

    fun updateCurrencyExchangeState(newState: UiState<CurrencyExchangeUiModel>) {
        _currencyExchangeFlow.value = newState
    }
}