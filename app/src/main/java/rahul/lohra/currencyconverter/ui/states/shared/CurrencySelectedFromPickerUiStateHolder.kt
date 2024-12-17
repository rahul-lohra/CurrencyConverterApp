package rahul.lohra.currencyconverter.ui.states.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rahul.lohra.currencyconverter.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencySelectedFromPickerUiStateHolder @Inject constructor(){
    private val _sharedState = MutableStateFlow(Constants.DEFAULT_CURRENCY)
    val sharedState: StateFlow<String> = _sharedState

    suspend fun updateState(newValue: String) {
        _sharedState.emit(newValue)
    }
}