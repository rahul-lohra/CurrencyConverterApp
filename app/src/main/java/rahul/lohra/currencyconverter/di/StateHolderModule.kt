package rahul.lohra.currencyconverter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rahul.lohra.currencyconverter.ui.states.shared.BaseCurrencyStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.CurrencyExchangeSharedState
import rahul.lohra.currencyconverter.ui.states.shared.CurrencySelectedFromPickerUiStateHolder
import rahul.lohra.currencyconverter.ui.states.shared.EnteredAmountSharedState
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StateHolderModule {

    @Singleton
    @Provides
    fun provideCurrencyStateHolder(): BaseCurrencyStateHolder {
        return BaseCurrencyStateHolder()
    }

    @Singleton
    @Provides
    fun provideCurrencySelectedStateHolder(): CurrencySelectedFromPickerUiStateHolder {
        return CurrencySelectedFromPickerUiStateHolder()
    }

    @Singleton
    @Provides
    fun provideCurrencyExchangeSharedState(): CurrencyExchangeSharedState {
        return CurrencyExchangeSharedState()
    }

    @Singleton
    @Provides
    fun provideEnteredAmountSharedState(): EnteredAmountSharedState {
        return EnteredAmountSharedState()
    }
}
