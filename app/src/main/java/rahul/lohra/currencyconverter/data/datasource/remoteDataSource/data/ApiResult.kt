package rahul.lohra.currencyconverter.data.datasource.remoteDataSource.data

sealed class ApiResult<T>
class ApiResultSuccess<T>(val data: T) : ApiResult<T>()
class ApiResultFail<T>(val ex: Throwable) : ApiResult<T>()