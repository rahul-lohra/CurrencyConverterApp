package rahul.lohra.currencyconverter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rahul.lohra.currencyconverter.data.repository.ExchangeRateRepository
import rahul.lohra.currencyconverter.domain.CurrencyExchangeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideCurrencyExchangeUseCase(
        repository: ExchangeRateRepository
    ): CurrencyExchangeUseCase {
        return CurrencyExchangeUseCase(repository)
    }
}