package rahul.lohra.currencyconverter.domain

sealed class UseCaseResult<out T>

data class UseCaseResultSuccess<out T>(val data: T) : UseCaseResult<T>()
data class UseCaseResultFailure(val exception: Throwable) : UseCaseResult<Nothing>()
