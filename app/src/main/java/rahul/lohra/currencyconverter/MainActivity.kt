package rahul.lohra.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import rahul.lohra.currencyconverter.ui.grid.compose.CurrencyExchangeGrid
import rahul.lohra.currencyconverter.ui.picker.compose.CurrencyPickerDialog
import rahul.lohra.currencyconverter.ui.states.UiFail
import rahul.lohra.currencyconverter.ui.states.UiInitial
import rahul.lohra.currencyconverter.ui.states.UiLoading
import rahul.lohra.currencyconverter.ui.states.UiSuccess
import rahul.lohra.currencyconverter.ui.theme.CurrencyConverterRahulTheme
import rahul.lohra.currencyconverter.viewmodels.CurrencyExchangeViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterRahulTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                        title = {
                            Text("Currency Convertor")
                        })
                }) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                    {
                        CurrencyExchangeUi()
                    }
                }
            }
        }
    }
}


@Composable
fun CurrencyExchangeUi(modifier: Modifier = Modifier) {
    Column() {
        val viewModel: CurrencyExchangeViewModel = hiltViewModel(key = CurrencyExchangeViewModel.KEY)
        val currencyRatesState by viewModel.currencyExchangeFlow.collectAsState()

        when (currencyRatesState) {
            is UiLoading -> {
                Loader(modifier)
            }

            is UiInitial -> {
                Loader(modifier)
            }

            is UiSuccess -> {
                CurrencyInputField(modifier)
                BaseCurrencyRow(modifier)
                CurrencyExchangeGrid()
            }

            is UiFail -> {
                ErrorScreen(modifier, (currencyRatesState as UiFail).message)
            }
        }
    }
}

@Composable
fun Loader(modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(modifier: Modifier, errorMessage:String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        Text(
            text = errorMessage,
            color = Color.Red
        )
    }
}

@Composable
fun CurrencyInputField(modifier: Modifier) {
    val viewModel: CurrencyExchangeViewModel = viewModel(key = CurrencyExchangeViewModel.KEY)
    val amount by viewModel.enteredAmountFlow.collectAsState()
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = amount,
        placeholder = { Text(text = "Enter your amount here") },
        onValueChange = { newText ->
            viewModel.onAmountChange(newText)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
fun BaseCurrencyRow(modifier: Modifier) {
    val viewModel: CurrencyExchangeViewModel = viewModel(key = CurrencyExchangeViewModel.KEY)
    val baseCurrency = viewModel.baseCurrencyStateHolder.sharedState.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        CurrencyPickerDialog(
            onDismissRequest = {
                showDialog = false
            },
            onConfirmation = {
                showDialog = false
            },
            "Pick your Currency"
        )
    }
    Row(
        horizontalArrangement = Arrangement.End, modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(modifier = Modifier.width(120.dp),
            onClick = { showDialog = true }
        ) {
            Text(baseCurrency)
            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = stringResource(id = R.string.drop_down)
            )
        }
    }
}
