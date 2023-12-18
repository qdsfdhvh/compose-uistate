import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ui.HomeScene

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xC2B280),
        ),
    ) {
        HomeScene()
    }
}
