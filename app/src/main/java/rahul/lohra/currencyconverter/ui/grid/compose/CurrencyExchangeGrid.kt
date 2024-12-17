package rahul.lohra.currencyconverter.ui.grid.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rahul.lohra.currencyconverter.ui.grid.viewmodels.CurrencyGridViewModel
import rahul.lohra.currencyconverter.ui.states.UiSuccess

@Composable
fun CurrencyExchangeGrid() {

    val vm: CurrencyGridViewModel = hiltViewModel(key = CurrencyGridViewModel.KEY)

    val dataListState = vm.convertedRateUiList.collectAsState().value
    if (dataListState is UiSuccess) {
        val dataList = dataListState.data
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxHeight(),
            content = {
                items(dataList.size, key = {
                    dataList[it].countryCode
                }, itemContent = { index ->
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .border(width = 1.dp, color = Color.Gray),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dataList[index].convertedRate,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(dataList[index].countryCode)
                    }

                })
            })
    }
}