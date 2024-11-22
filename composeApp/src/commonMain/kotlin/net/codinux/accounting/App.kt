package net.codinux.accounting

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.screens.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

private val typography = Typography(
    body1 = TextStyle(fontSize = 14.sp, color = Colors.Zinc700)
)

@Composable
@Preview
fun App() {

    val colors = MaterialTheme.colors.copy(primary = Colors.Primary, primaryVariant = Colors.PrimaryDark, onPrimary = Color.White,
        secondary = Colors.Accent, secondaryVariant = Colors.Accent, onSecondary = Color.White)


    MaterialTheme(colors = colors, typography = typography) {
        MainScreen()
    }
}