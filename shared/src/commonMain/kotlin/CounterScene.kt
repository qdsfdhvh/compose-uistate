import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seiko.uistate.UiState
import com.seiko.uistate.map
import com.seiko.uistate.onLoading
import com.seiko.uistate.onSuccess
import kotlinx.coroutines.delay
import moe.tlaster.precompose.molecule.producePresenter
import kotlin.random.Random

@Composable
fun CounterScene() {
    val uiState by producePresenter { CounterPresenter() }
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            uiState.onSuccess { state ->
                Text(text = state.count)
                Button(
                    onClick = {
                        state.event(CounterEvent.Increment)
                    }
                ) {
                    Text(text = "Increment")
                }
                Button(
                    onClick = {
                        state.event(CounterEvent.Decrement)
                    }
                ) {
                    Text(text = "Decrement")
                }
            }.onLoading {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun CounterPresenter(): UiState<CounterState> {
    val initValue by produceState(UiState.loading()) {
        delay(500)
        value = UiState.success(Random.nextInt(100))
    }
    return initValue.map {
        var count by remember { mutableStateOf(it) }
        CounterState(count.toString()) { event ->
            when (event) {
                CounterEvent.Increment -> count++
                CounterEvent.Decrement -> count--
            }
        }
    }
}

private data class CounterState(
    val count: String,
    val event: (CounterEvent) -> Unit
)

private sealed interface CounterEvent {
    object Increment : CounterEvent
    object Decrement : CounterEvent
}
