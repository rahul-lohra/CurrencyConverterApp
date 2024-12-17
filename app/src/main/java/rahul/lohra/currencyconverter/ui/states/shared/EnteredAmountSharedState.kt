package rahul.lohra.currencyconverter.ui.states.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnteredAmountSharedState @Inject constructor() {
    private val _enteredAmountFlow = MutableStateFlow("")
    val enteredAmountFlow: StateFlow<String> = _enteredAmountFlow

    fun updateEnteredAmount(newAmount: String) {
        _enteredAmountFlow.value = newAmount
    }
}