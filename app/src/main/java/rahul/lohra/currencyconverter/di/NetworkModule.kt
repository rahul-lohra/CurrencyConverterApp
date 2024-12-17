package rahul.lohra.currencyconverter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.ExchangeRateNetworkClient
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideExchangeRateNetworkClient(): OpenExchangeRateNetworkApi {
        return ExchangeRateNetworkClient.provideNetworkClient()
    }
}