import androidx.compose.ui.window.ComposeUIViewController
import moe.tlaster.precompose.PreComposeApp

fun MainViewController() = ComposeUIViewController {
    PreComposeApp {
        App()
    }
}
