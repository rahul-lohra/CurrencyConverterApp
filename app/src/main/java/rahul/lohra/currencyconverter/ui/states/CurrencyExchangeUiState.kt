package rahul.lohra.currencyconverter.ui.states


sealed class CurrencyExchangeUiState
class CurrencyExchangeUiFullState(data: CurrencyExchangeUiModel) : CurrencyExchangeUiState()
data object CurrencyExchangeUiEmptyState : CurrencyExchangeUiState()

data class CurrencyExchangeUiModel(val code: String, val rates: Map<String, Float>)