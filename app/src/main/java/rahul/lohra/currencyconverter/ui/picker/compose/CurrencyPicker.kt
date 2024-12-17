package rahul.lohra.currencyconverter.ui.picker.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import rahul.lohra.currencyconverter.ui.picker.state.CurrencyListItem
import rahul.lohra.currencyconverter.ui.picker.viewmodels.CurrencyPickerViewModel
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiState
import rahul.lohra.currencyconverter.ui.states.UiSuccess

@Composable
fun CurrencyPickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String
) {
    val vm: CurrencyPickerViewModel = viewModel(key = CurrencyPickerViewModel.KEY)

    AlertDialog(modifier = Modifier.padding(vertical = 50.dp),
        title = {
            Text(text = dialogTitle)
        },
        text = {
            CurrencyListUi(Modifier)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    vm.confirmSelectedCurrency()
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun CurrencyListUi(modifier: Modifier) {
    val vm: CurrencyPickerViewModel = viewModel(key = CurrencyPickerViewModel.KEY)

    val state2: UiState<List<CurrencyListItem>> by vm.currencyPickerUiList.collectAsState()

    if (state2 is UiInitial<*>) {
        Text("Loading....")
    } else if (state2 is UiSuccess<List<CurrencyListItem>>) {
        val messages = (state2 as UiSuccess<List<CurrencyListItem>>).data

        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(messages.size, key = { index -> messages[index].countryCode }) { index ->
                Column() {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(messages[index].countryCode, modifier = modifier.fillMaxWidth())
                        RadioButton(messages[index].isSelected, onClick = {
                            vm.selectCurrencyFromPickerList(messages[index].countryCode)
                        })
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}