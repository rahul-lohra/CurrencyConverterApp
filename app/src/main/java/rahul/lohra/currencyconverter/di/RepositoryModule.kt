package rahul.lohra.currencyconverter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.network.OpenExchangeRateNetworkApi
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.ExchangeRemoteDataSource
import rahul.lohra.currencyconverter.data.datasource.remoteDataSource.ExchangeRemoteDataSourceImpl
import rahul.lohra.currencyconverter.data.repository.ExchangeRateRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideExchangeRemoteDataSource(client: OpenExchangeRateNetworkApi): ExchangeRemoteDataSource {
        return ExchangeRemoteDataSourceImpl(client)
    }

    @Singleton
    @Provides
    fun provideExchangeRateRepository(
        remoteDataSource: ExchangeRemoteDataSource
    ): ExchangeRateRepository {
        return ExchangeRateRepository(remoteDataSource)
    }
}