package rahul.lohra.currencyconverter.ui.states

sealed class UiState<T>
class UiInitial<T> : UiState<T>() {
    override fun equals(other: Any?) = other is UiLoading<*>
    override fun hashCode() = javaClass.hashCode()
}
data class UiSuccess<T>(val data: T) : UiState<T>()
class UiLoading<T> : UiState<T>() {
    override fun equals(other: Any?) = other is UiLoading<*>
    override fun hashCode() = javaClass.hashCode()
}
data class UiFail<T>(val message:String) : UiState<T>()