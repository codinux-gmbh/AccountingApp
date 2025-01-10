package net.codinux.accounting

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.platform.IoOrDefault
import net.codinux.accounting.platform.PlatformUiFunctions
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.screens.MainScreen
import net.codinux.kotlin.Platform
import net.codinux.kotlin.PlatformType
import net.codinux.log.LoggerFactory
import org.jetbrains.compose.ui.tooling.preview.Preview

private val typography = Typography(
    body1 = TextStyle(fontSize = 14.sp, color = Colors.BodyTextColor)
)

@Composable
@Preview
fun App() {
    LoggerFactory.config.defaultLoggerName = "net.codinux.accounting"
    LoggerFactory.debugConfig.useCallerMethodIfLoggerNameNotSet = true

    val colors = MaterialTheme.colors.copy(primary = Colors.Primary, primaryVariant = Colors.PrimaryDark, onPrimary = Color.White,
        secondary = Colors.Accent, secondaryVariant = Colors.Accent, onSecondary = Color.White)

    val screenSize = PlatformUiFunctions.rememberScreenSize()

    DI.uiState.screenSizeChanged(screenSize)

    var isInitialized by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    MaterialTheme(colors = colors, typography = typography) {
        MainScreen()
    }


    LaunchedEffect(isInitialized) {
        if (isInitialized == false) {
            isInitialized = true

            coroutineScope.launch(Dispatchers.IoOrDefault) {
                DI.init()
            }

            if (Platform.type == PlatformType.iOS) {
                PlatformUiFunctions.addKeyboardVisibilityListener { visible ->
                    DI.uiState.isKeyboardVisible.value = visible
                }
            }
        }
    }
}